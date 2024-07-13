package com.yutak.im.store;

import com.yutak.im.domain.Conversation;
import com.yutak.im.domain.Message;
import com.yutak.im.proto.CS;
import com.yutak.vertx.kit.StringKit;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class YutakStore {

    public RocksDB db;
    public final ThreadPoolExecutor executor; //executor pool
    // just for read, not need ConcurrentHashMap
    public final List<ColumnFamilyHandle> slotHandles;
    private final Logger log = LoggerFactory.getLogger(YutakStore.class);
    private final WriteOptions wOps;
    private final ReadOptions rOps;
    private final static YutakStore yutakStore;
    static {
        // build local Store
        yutakStore = new YutakStore();
    }

    private YutakStore() {
        executor = new ThreadPoolExecutor(5, 10, 200, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        // index slotNum means default ColumFamilyHandle
        slotHandles = new ArrayList<>(Config.slotNum+1);

        RocksDB.loadLibrary();
        // init write options
        wOps = new WriteOptions();

        wOps.setSync(false);
        // init ReadOptions
        rOps = new ReadOptions();

        init();
    }
    public static YutakStore get() {
        return yutakStore;
    }
    private void init() {
        Options options = null;
        DBOptions dbOptions = null;
        try {
            if (Config.isFirstStart) {
                options = new Options();
                options.setCreateIfMissing(true);
                db = RocksDB.open(options,Config.dataDir);
                for (int i = 0; i < Config.slotNum; i++) {
                    ColumnFamilyDescriptor c = new ColumnFamilyDescriptor(String.valueOf(i).getBytes(StandardCharsets.UTF_8));
                    ColumnFamilyHandle handle = db.createColumnFamily(c);
                    slotHandles.add(i,handle);
                }
                ColumnFamilyHandle handle = db.createColumnFamily(new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY));
                // add default columnFamilyHandle
                slotHandles.add(Config.slotNum,handle);
                log.debug("yutak ==> build file store success");
            } else {
                // load ColumnFamilies
                options = new Options();
                List<byte[]> bytes = RocksDB.listColumnFamilies(options, Config.dataDir);

                // build data in memory
                List<ColumnFamilyDescriptor> descriptors = new ArrayList<>(bytes.size());
                // ColumnFamilyHandler will be filled data after RocksDB opened
                List<ColumnFamilyHandle> handles = new ArrayList<>();
                for (byte[] b : bytes) {
                    ColumnFamilyDescriptor c = new ColumnFamilyDescriptor(b);
                    descriptors.add(c);
                }
                // db options
                dbOptions = new DBOptions();
                dbOptions.setCreateIfMissing(true);
                dbOptions.setCreateMissingColumnFamilies(false);

                // open rocksDB
                db = RocksDB.open(dbOptions, Config.dataDir, descriptors, handles);
                // create handler and descriptor in memory
                for (ColumnFamilyHandle handle : handles) {
                    // add  default column family
                    if (handle.getName().length > 4){
//                        slotHandles.add(Config.slotNum,handle);
                        continue;
                    }
                    slotHandles.add(Integer.parseInt(new String(handle.getName())), handle);
                }
                slotHandles.add(Config.slotNum,handles.get(0));
                //
                log.debug("yutak ==> Load file store successfully");
            }
        } catch (Exception e) {
            log.error("File store start error,cause:{}",e.getMessage());
            if(e instanceof RocksDBException) {
                options.close();
                if (dbOptions != null) dbOptions.close();
                if(db != null) db.close();
                slotHandles.clear();
            }
        }
    }
    public void destroy() {
        executor.shutdown();
        slotHandles.forEach(ColumnFamilyHandle::close);
        slotHandles.clear();
        log.debug("yutak ==> destroy file store success");
    }
    private ColumnFamilyHandle getHandle(int slot) {
        return slotHandles.get(slot);
    }
    public CompletableFuture<ChannelInfo> getChannelAsync(String channelID,byte channelType) {
        return CompletableFuture.supplyAsync(()-> getChannel(channelID,channelType),executor);
    }
    public ChannelInfo getChannel(String channelID,int channelType) {
        if (channelID == null || channelID.length() == 0) return null;
        byte[] V = getV(Kit.slotNum(channelID), Kit.buildChannelKey(channelID, channelType));
        if(V == null) {
            return null;
        }
        ChannelInfo c = new ChannelInfo();
        c.ban = V[0];
        c.large = V[1];
        c.disband = V[2];
        return c;
    }
    public JsonObject getUserToken(String uid,byte deviceFlag){
        if (uid == null) return null;
        byte[] v = getV(Kit.slotNum(uid), Kit.buildUserTokenKey(uid, deviceFlag));
        if(v == null) {
            return null;
        }
        return Kit.decodeObj(v);
    }
    public CompletableFuture<JsonObject> getUserTokenAsync(String uid,byte deviceFlag) {
        return CompletableFuture.supplyAsync(()-> getUserToken(uid,deviceFlag),executor);
    }
    public CompletableFuture<Void> updateUserTokenAsync(String uid,int deviceFlag,byte deviceLevel,String token) {
        return CompletableFuture.runAsync(()->{
          updateUserToken(uid,deviceFlag,deviceLevel,token);
        },executor);
    }
    public void updateUserToken(String uid,int deviceFlag,byte deviceLevel,String token) {
        if (uid == null) return;
        JsonObject o = new JsonObject();
        o.put("deviceLevel",deviceLevel);
        o.put("token",token);
        putKV(Kit.slotNum(uid),Kit.buildUserTokenKey(uid, deviceFlag),Kit.encode(o));
    }
    public CompletableFuture<Void> addOrUpdateChannel(ChannelInfo channelInfo) {
        return CompletableFuture.runAsync(()->{
            if (channelInfo == null || channelInfo.channelId == null || channelInfo.channelId.length() == 0) return;
            byte[] v = new byte[3];
            v[0] = channelInfo.ban;
            v[1] = channelInfo.large;
            v[2] = channelInfo.disband;
            putKV(Kit.slotNum(channelInfo.channelId),Kit.buildChannelKey(channelInfo.channelId, channelInfo.channelType),v);
        },executor);
    }
    public CompletableFuture<Boolean> existsChannel(String channelID,byte channelType) {
        return CompletableFuture.supplyAsync(()->{
            byte[] v = getV(Kit.slotNum(channelID), Kit.buildChannelKey(channelID, channelType));
            return v != null;
        },executor);
    }
    public CompletableFuture<Void> removeChannel(String channelID,byte channelType) {
        return CompletableFuture.runAsync(()->{
            delK(Kit.slotNum(channelID), Kit.buildChannelKey(channelID, channelType));
        },executor);
    }
    public void addSubscribers(String channelID,byte channelType,List<String> uids){
        putList(Kit.slotNum(channelID),Kit.buildSubscribeKey(channelID,channelType),uids);
    }
    public CompletableFuture<Void> addSubscribersAsync(String channelID,byte channelType,List<String> uids) {
        return CompletableFuture.runAsync(()->{
            addSubscribers(channelID,channelType,uids);
        },executor);
    }
    public void removeSubscribers(String channelID,byte channelType,List<String> uids){
        if (channelID == null || channelID.isEmpty() || uids == null || uids.isEmpty()) return;
        delList(Kit.slotNum(channelID),Kit.buildSubscribeKey(channelID, channelType),uids);
    }
    public CompletableFuture<Void> removeSubscribersAsync(String channelID,byte channelType,List<String> uids) {
        return CompletableFuture.runAsync(()->{
            removeSubscribers(channelID,channelType,uids);
            },executor);
    }
    public CompletableFuture<List<String>> getSubscribersAsync(String channelID,byte channelType) {
        return CompletableFuture.supplyAsync(()->{
            return  getList(Kit.slotNum(channelID), Kit.buildSubscribeKey(channelID, channelType));
        },executor);
    }
    public List<String> getSubscribers(String channelID,int channelType) {
        return getList(Kit.slotNum(channelID), Kit.buildSubscribeKey(channelID, channelType));
    }
    public void removeAllSubscribers(String channelID,int channelType){
        delK(Kit.slotNum(channelID), Kit.buildSubscribeKey(channelID, channelType));
    }
    public CompletableFuture<Void> removeAllSubscribersAsync(String channelID,int channelType) {
        return CompletableFuture.runAsync(()->{
            removeAllSubscribers(channelID,channelType);
        },executor);
    }
    public CompletableFuture<List<String>> getAllowListAsync(String channelID,int channelType) {
        return CompletableFuture.supplyAsync(()->{
            return  getList(Kit.slotNum(channelID), Kit.buildAllowListKey(channelID, channelType));
        },executor);
    }
    public List<String> getAllowList(String channelID,int channelType) {
        return  getList(Kit.slotNum(channelID), Kit.buildAllowListKey(channelID, channelType));
    }
    public CompletableFuture<Void> addAllowList(String channelID,byte channelType,List<String> uids) {
        return CompletableFuture.runAsync(()->{
            putList(Kit.slotNum(channelID),Kit.buildAllowListKey(channelID,channelType),uids);
        },executor);
    }
    public CompletableFuture<Void> removeAllowList(String channelID,byte channelType,List<String> uids) {
        return CompletableFuture.runAsync(()->{
           delList(Kit.slotNum(channelID),Kit.buildAllowListKey(channelID, channelType),uids);
        },executor);
    }
    public CompletableFuture<Void> removeAllAllowList(String channelID,byte channelType) {
        return CompletableFuture.runAsync(()->{
            delK(Kit.slotNum(channelID), Kit.buildAllowListKey(channelID, channelType));
        },executor);
    }
    public CompletableFuture<List<String>> getDenyListAsync(String channelID,byte channelType) {
        return CompletableFuture.supplyAsync(()->{
            return getList(Kit.slotNum(channelID), Kit.buildDenyListKey(channelID, channelType));
        },executor);
    }
    public List<String> getDenyList(String channelID,int channelType) {
        return getList(Kit.slotNum(channelID), Kit.buildDenyListKey(channelID, channelType));
    }
    public CompletableFuture<Void> addDenyList(String channelID,byte channelType,List<String> uids) {
        return CompletableFuture.runAsync(()->{
            putList(Kit.slotNum(channelID),Kit.buildDenyListKey(channelID, channelType),uids);
        },executor);
    }
    public CompletableFuture<Void> removeDenyList(String channelID,byte channelType,List<String> uids) {
        return CompletableFuture.runAsync(()->{
            delList(Kit.slotNum(channelID), Kit.buildDenyListKey(channelID, channelType),uids);
        },executor);
    }
    public CompletableFuture<Void> appendMessageOfNotifyQueueAsync(List<Message> messages) {
        return CompletableFuture.runAsync(()->{

        },executor);
    }
    public List<Message> getMessageOfNotifyQueue(int count) {
        return null;
    }
    public CompletableFuture<List<Message>> getMessageOfNotifyQueueAsync(int count) {
        return CompletableFuture.supplyAsync(()->{
            return null;
        },executor);
    }
    public void appendMessageOfNotifyQueue(List<Message> messages) {

    }

    public void removeMessagesOfNotifyQueue(List<Long> messagesIDs) {

    }
    public CompletableFuture<Void> removeMessagesOfNotifyQueueAsync(List<Long> messagesIDs) {
        return CompletableFuture.runAsync(()->{

        },executor);
    }
    public CompletableFuture<Void> removeAllDenyList(String channelID,byte channelType) {
        return CompletableFuture.runAsync(()->{
            delK(Kit.slotNum(channelID), Kit.buildDenyListKey(channelID, channelType));
        },executor);
    }
    public CompletableFuture<Void> addSystemUIDs(List<String> uids) {
        return CompletableFuture.runAsync(()->{
            // put into default columnFamily
            putList(Config.slotNum,Config.systemUIDsKey,uids);
        },executor);
    }
    public CompletableFuture<Void> removeSystemUIDs(List<String> uids) {
        return CompletableFuture.runAsync(()->{
            delList(Config.slotNum,Config.systemUIDsKey,uids);
        },executor);
    }
    public CompletableFuture<List<String>> getSystemUIDs() {
        return CompletableFuture.supplyAsync(()-> getList(Config.slotNum,Config.systemUIDsKey),executor);
    }
    public CompletableFuture<Void> addIPBlockList(List<String> ips) {
        return CompletableFuture.runAsync(()->{
            putList(Config.slotNum,Config.ipBlacklistKey,ips);
        },executor);
    }
    public CompletableFuture<Void> removeIPBlockList(List<String> ips) {
        return CompletableFuture.runAsync(()->{
            if (ips == null || ips.size() == 0) return;
            delList(Config.slotNum,Config.ipBlacklistKey,ips);
        },executor);
    }
    public CompletableFuture<List<String>> getIPBlockList() {
        return CompletableFuture.supplyAsync(()-> getList(Config.slotNum,Config.ipBlacklistKey),executor);
    }
    public CompletableFuture<Void> appendMessageOfUser(String uid,List<Message> message) {
        return CompletableFuture.runAsync(()->{
            // TODO  :  not impl right now
        },executor);
    }
    public List<Conversation> getConversations(String uid) {
        if (StringKit.isEmpty(uid)) {
            return null;
        }
        return getList(Kit.slotNum(uid),Kit.buildConversationKey(uid));
    }
    public CompletableFuture<List<Conversation>> getConversationsAsync(String uid) {
        return CompletableFuture.supplyAsync(()-> getConversations(uid),executor);
    }
    public Conversation getConversation(String uid,String channelID,int channelType) {
        List<Conversation> conversations = getConversations(uid);
        if (conversations == null || conversations.isEmpty()) return null;
        for (Conversation conversation : conversations) {
            if (StringKit.same(channelID,conversation.channelID)&&channelType == conversation.channelType) {
                return conversation;
            }
        }
        return null;
    }
    public CompletableFuture<Conversation> getConversationAsync(String uid,String channelID,int channelType) {
        return CompletableFuture.supplyAsync(()-> getConversation(uid,channelID,channelType),executor);
    }

    public void deleteConversation(String uid,String channelID,int channelType) {
        List<Conversation> list = getConversations(uid);
        if(list == null||list.isEmpty()){
            return;
        }
        List<Conversation> update = new ArrayList<>();
        for (Conversation conversation : list) {
            if (!StringKit.same(conversation.channelID,channelID) && conversation.channelType == channelType) {
                update.add(conversation);
            }
        }
        putList(Kit.slotNum(uid),Kit.buildConversationKey(uid),update);
    }
    public CompletableFuture<Void> deleteConversationAsync(String uid,String channelID,int channelType) {
        return CompletableFuture.runAsync(()->{
            deleteConversation(uid,channelID,channelType);
        },executor);
    }
    public  List<Conversation> addOrUpdateConversations(String uid,List<Conversation> updateConversations) {
        // no conversation
        List<Conversation> oldConversations = getConversations(uid);
        if (oldConversations == null || oldConversations.isEmpty()) {
            putList(Kit.slotNum(uid),Kit.buildConversationKey(uid),updateConversations);
            return updateConversations;
        }
        // has old conversation info
        List<Conversation> newConversations = new ArrayList<>(oldConversations.size()+updateConversations.size());
        newConversations.addAll(oldConversations);
        for (Conversation update : updateConversations) {
            int index = -1;
            for (int i = 0; i < oldConversations.size(); i++) {
                Conversation old = oldConversations.get(i);
                if (StringKit.same(old.channelID, update.channelID) && old.channelType == update.channelType) {
                        index = i;
                }
            }
            if (index != -1) {
                // update conversation
                newConversations.add(index, update);
            }else{
                // add conversation
                newConversations.add(update);
            }
        }
        return newConversations;
    }
    public CompletableFuture<List<Conversation>> addOrUpdateConversationsAsync(String uid,List<Conversation> updateConversations) {
        return CompletableFuture.supplyAsync(()->addOrUpdateConversations(uid,updateConversations),executor);
    }
    private void delList(int slotNum,byte[] K,List list) {
        byte[] v = getV(slotNum, K);
        JsonArray o = Kit.decodeArray(v);
        JsonArray r = new JsonArray();
        r.addAll(o);
        o.forEach(k->{
            if(list.contains(k)) {
                r.remove(k);
            }
        });
        putKV(slotNum,K,Kit.encode(r));
    }
    private void putList(int slotNum,byte[] K,List list) {
        byte[] v = getV(slotNum, K);
        JsonArray o = null;
        if (v == null || v.length == 0){
            o = new JsonArray();
        } else {
            o = Kit.decodeArray(v);
        }
        list.forEach(o::add);
        putKV(slotNum,K,Kit.encode(o));
    }
    private List getList(int slotNum,byte[] K) {
        byte[] v = getV(slotNum, K);
        if(v == null) {
            return null;
        }
        return Kit.decodeArray(v).getList();
    }
    private void putKV(int slot,byte[] K,byte[] V) {
        try {
            db.put(slotHandles.get(slot),wOps,K,V);
        } catch (RocksDBException e) {
            log.error("putK error,(k,{}),cause:{}",new String(K),e.getMessage());
        }
    }
    private byte[] getV(int slot,byte[] K) {
        byte[] V = null;
        try {
            V = db.get(slotHandles.get(slot),rOps,K);
        }catch (RocksDBException e) {
            log.error("getK error,(k,{}),cause:{}",new String(K),e.getMessage());
        }
        return V;
    }
    private void delK(int slot,byte[] K) {
        try {
            db.delete(slotHandles.get(slot),wOps,K);
        } catch (RocksDBException e) {
            log.error("delK error,(k,{}),cause:{}",new String(K),e.getMessage());
        }
    }
    public static void main(String[] args) {

        List<Conversation> conversations = new ArrayList<>();
        int t = 1001;
        for (int i = 0; i < 4; i++) {
            Conversation c = new Conversation();
            c.channelID = String.valueOf(t);
            c.channelType = CS.ChannelType.Person;
            c.unreadCount = 1;
            conversations.add(c);
            t++;
        }
        printList(conversations);
        List<Conversation> updateConversations = new ArrayList<>();
        int x = 1003;
        for (int i = 0; i < 4; i++) {
            Conversation c = new Conversation();
            c.channelID = String.valueOf(x);
            c.channelType = CS.ChannelType.Person;
            c.unreadCount = 2;
            updateConversations.add(c);
        }
        printList(updateConversations);
        YutakStore yutakStore1 = new YutakStore();
    }
    private static void printList(List<Conversation> conversations) {
        for (Conversation conversation : conversations) {
            System.out.println(conversation.channelID+"unreadCount:"+conversation.unreadCount);
        }
    }
}