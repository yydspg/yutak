package com.yutak.im.store;

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
        yutakStore = new YutakStore();
    }

    private YutakStore() {
        executor = new ThreadPoolExecutor(10, 30, 200, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
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
                    String s = new String(b, StandardCharsets.UTF_8);
                    System.out.println(s);
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
                        slotHandles.add(Config.slotNum,handle);
                        continue;
                    }
                    slotHandles.add(Integer.parseInt(new String(handle.getName())), handle);
                }
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
    public ChannelInfo getChannel(String channelID,byte channelType) {
        if (channelID == null || channelID.length() == 0) return null;
        byte[] V = getV(Kit.slotNum(channelID), Kit.buildChannelKey(channelID, channelType));
        if(V == null) {
            return null;
        }
        ChannelInfo c = new ChannelInfo();
        c.channelId = channelID;
        c.channelType = channelType;
        c.ban = V[0];
        c.large = V[1];
        c.disband = V[2];
        return c;
    }
    public CompletableFuture<JsonObject> getUserToken(String uid,byte deviceFlag) {
        return CompletableFuture.supplyAsync(()->{
            if (uid == null) return null;
            byte[] v = getV(Kit.slotNum(uid), Kit.buildUserTokenKey(uid, deviceFlag));
            if(v == null) {
                return null;
            }
            return Kit.decodeObj(v);
        });
    }
    public CompletableFuture<Void> updateUserToken(String uid,byte deviceFlag,byte deviceLevel,String token) {
        return CompletableFuture.runAsync(()->{
            if (uid == null) return;
            JsonObject o = new JsonObject();
            o.put("deviceLevel",deviceLevel);
            o.put("token",token);
            putKV(Kit.slotNum(uid),Kit.buildUserTokenKey(uid, deviceFlag),Kit.encode(o));
        });
    }
    public CompletableFuture<Void> addOrUpdateChannel(ChannelInfo channelInfo) {
        return CompletableFuture.runAsync(()->{
            if (channelInfo == null || channelInfo.channelId == null || channelInfo.channelId.length() == 0) return;
            byte[] v = new byte[3];
            v[0] = channelInfo.ban;
            v[1] = channelInfo.large;
            v[2] = channelInfo.disband;
            putKV(Kit.slotNum(channelInfo.channelId),Kit.buildChannelKey(channelInfo.channelId, channelInfo.channelType),v);
        });
    }
    public CompletableFuture<Boolean> existsChannel(String channelID,byte channelType) {
        return CompletableFuture.supplyAsync(()->{
            if (channelID == null || channelID.length() == 0) return false;
            byte[] v = getV(Kit.slotNum(channelID), Kit.buildChannelKey(channelID, channelType));
            return v != null;
        });
    }
    public CompletableFuture<Void> removeChannel(String channelID,byte channelType) {
        return CompletableFuture.runAsync(()->{
            if (channelID == null || channelID.length() == 0) return;
            delK(Kit.slotNum(channelID), Kit.buildChannelKey(channelID, channelType));
        });
    }
    public CompletableFuture<Void> addSubscribers(String channelID,byte channelType,List<String> uids) {
        return CompletableFuture.runAsync(()->{
           if (channelID == null || channelID.length() == 0 || uids == null || uids.size() == 0) return;
            putList(Kit.slotNum(channelID),Kit.buildSubscribeKey(channelID,channelType),uids);
        });
    }
    public CompletableFuture<Void> removeSubscribers(String channelID,byte channelType,List<String> uids) {
            return CompletableFuture.runAsync(()->{
                if (channelID == null || channelID.length() == 0 || uids == null || uids.size() == 0) return;
                delList(Kit.slotNum(channelID),Kit.buildSubscribeKey(channelID, channelType),uids);
            });
    }
    public CompletableFuture<List<String>> getSubscribersAsync(String channelID,byte channelType) {
        return CompletableFuture.supplyAsync(()->{
            if (channelID == null || channelID.length() == 0) return null;
            return  getList(Kit.slotNum(channelID), Kit.buildSubscribeKey(channelID, channelType));
        });
    }
    public List<String> getSubscribers(String channelID,byte channelType) {
        if (channelID == null || channelID.length() == 0) return null;
        return getList(Kit.slotNum(channelID), Kit.buildSubscribeKey(channelID, channelType));
    }
    public CompletableFuture<Void> removeAllSubscribers(String channelID,byte channelType) {
        return CompletableFuture.runAsync(()->{
            if (channelID == null || channelID.length() == 0) return;
            delK(Kit.slotNum(channelID), Kit.buildSubscribeKey(channelID, channelType));
        });
    }
    public CompletableFuture<List<String>> getAllowListAsync(String channelID,byte channelType) {
        return CompletableFuture.supplyAsync(()->{
            if (channelID == null || channelID.length() == 0) return null;
            return  getList(Kit.slotNum(channelID), Kit.buildAllowListKey(channelID, channelType));
        });
    }
    public List<String> getAllowList(String channelID,byte channelType) {
        if (channelID == null || channelID.length() == 0) return null;
        return  getList(Kit.slotNum(channelID), Kit.buildAllowListKey(channelID, channelType));
    }
    public CompletableFuture<Void> addAllowList(String channelID,byte channelType,List<String> uids) {
        return CompletableFuture.runAsync(()->{
            if (channelID == null || channelID.length() == 0 || uids == null || uids.size() == 0) return;
            putList(Kit.slotNum(channelID),Kit.buildAllowListKey(channelID,channelType),uids);
        });
    }
    public CompletableFuture<Void> removeAllowList(String channelID,byte channelType,List<String> uids) {
        return CompletableFuture.runAsync(()->{
            if (channelID == null || channelID.length() == 0 || uids == null || uids.size() == 0) return;
           delList(Kit.slotNum(channelID),Kit.buildAllowListKey(channelID, channelType),uids);
        });
    }
    public CompletableFuture<Void> removeAllAllowList(String channelID,byte channelType) {
        return CompletableFuture.runAsync(()->{
            if (channelID == null || channelID.length() == 0 ) return;
            delK(Kit.slotNum(channelID), Kit.buildAllowListKey(channelID, channelType));
        });
    }
    public CompletableFuture<List<String>> getDenyListAsync(String channelID,byte channelType) {
        return CompletableFuture.supplyAsync(()->{
            if (channelID == null || channelID.length() == 0) return null;
            return getList(Kit.slotNum(channelID), Kit.buildDenyListKey(channelID, channelType));
        });
    }
    public List<String> getDenyList(String channelID,byte channelType) {
        if (channelID == null || channelID.length() == 0) return null;
        return getList(Kit.slotNum(channelID), Kit.buildDenyListKey(channelID, channelType));
    }
    public CompletableFuture<Void> addDenyList(String channelID,byte channelType,List<String> uids) {
        return CompletableFuture.runAsync(()->{
            if (channelID == null || channelID.length() == 0 || uids == null || uids.size() == 0) return;
            putList(Kit.slotNum(channelID),Kit.buildDenyListKey(channelID, channelType),uids);
        });
    }
    public CompletableFuture<Void> removeDenyList(String channelID,byte channelType,List<String> uids) {
        return CompletableFuture.runAsync(()->{
            if (channelID == null || channelID.length() == 0 || uids == null || uids.size() == 0) return;
            delList(Kit.slotNum(channelID), Kit.buildDenyListKey(channelID, channelType),uids);
        });
    }
    public CompletableFuture<Void> removeAllDenyList(String channelID,byte channelType) {
        return CompletableFuture.runAsync(()->{
            if (channelID == null || channelID.length() == 0) return;
            delK(Kit.slotNum(channelID), Kit.buildDenyListKey(channelID, channelType));
        });
    }
    public CompletableFuture<Void> addSystemUIDs(List<String> uids) {
        return CompletableFuture.runAsync(()->{
            if (uids == null || uids.size() == 0) return;
            // put into default columnFamily
            putList(Config.slotNum,Config.systemUIDsKey,uids);
        });
    }
    public CompletableFuture<Void> removeSystemUIDs(List<String> uids) {
        return CompletableFuture.runAsync(()->{
            if (uids == null || uids.size() == 0) return;
            delList(Config.slotNum,Config.systemUIDsKey,uids);
        });
    }
    public CompletableFuture<List<String>> getSystemUIDs() {
        return CompletableFuture.supplyAsync(()->{
            return getList(Config.slotNum,Config.systemUIDsKey);
        });
    }
    public CompletableFuture<Void> addIPBlockList(List<String> ips) {
        return CompletableFuture.runAsync(()->{
            if (ips == null || ips.size() == 0) return;
            putList(Config.slotNum,Config.ipBlacklistKey,ips);
        });
    }
    public CompletableFuture<Void> removeIPBlockList(List<String> ips) {
        return CompletableFuture.runAsync(()->{
            if (ips == null || ips.size() == 0) return;
            delList(Config.slotNum,Config.ipBlacklistKey,ips);
        });
    }
    public CompletableFuture<List<String>> getIPBlockList() {
        return CompletableFuture.supplyAsync(()->{
            return getList(Config.slotNum,Config.ipBlacklistKey);
        });
    }
    private void delList(int slotNum,byte[] K,List<String> list) {
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
    private void putList(int slotNum,byte[] K,List<String> list) {
        byte[] v = getV(slotNum, K);
        JsonArray o = Kit.decodeArray(v);
        list.forEach(o::add);
        putKV(slotNum,K,Kit.encode(o));
    }
    private List<String> getList(int slotNum,byte[] K) {
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

        YutakStore yutakStore = new YutakStore();
        yutakStore.putKV(1,"man".getBytes(),"what can i say".getBytes());
        yutakStore.getV(1,"nihao".getBytes());
        String s = new String(yutakStore.getV(1, "man".getBytes()));
        System.out.println(s);

        yutakStore.destroy();
//        ArrayList<String> s = new ArrayList<>();
//        s.add("wert");
//        s.add("yutak");
//        s.add("paul");
//        JsonArray a = new JsonArray(s);
////        a.remove("yutak");
////        a.remove("paul");
//        byte[] encode = Kit.encode(a);
//        JsonArray array = Kit.decodeArray(encode);
//        array.remove("yutak");
//        List list = array.getList();
//        list.forEach(System.out::println);
    }
}