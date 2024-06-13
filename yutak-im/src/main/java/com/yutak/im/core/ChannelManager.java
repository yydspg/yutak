package com.yutak.im.core;

import com.yutak.im.domain.CommonChannel;
import com.yutak.im.proto.CS;
import com.yutak.im.store.ChannelInfo;
import com.yutak.im.store.H2Store;
import com.yutak.im.store.Store;
import io.vertx.core.Vertx;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

// 频道管理
public class ChannelManager {
    public final String tmpChannelPrefix = "tmp";
    private Store store;
    private final ConcurrentHashMap<String, CommonChannel> channels;
    private final ConcurrentHashMap<String,CommonChannel> tmpChannels;
    private final ConcurrentHashMap<String, CommonChannel> dataChannels;
    private final ConcurrentHashMap<String, CommonChannel> personChannels;
    private Options options;
    public final Vertx vertx;
    private static ChannelManager instance;
    static {
        instance = new ChannelManager();
    }
    private ChannelManager() {
        this.channels = new ConcurrentHashMap<>();
        this.tmpChannels = new ConcurrentHashMap<>();
        this.dataChannels = new ConcurrentHashMap<>();
        this.personChannels = new ConcurrentHashMap<>();
        store = H2Store.get();
        options = Options.get();
        vertx = YutakNetServer.get().vertx;
    }

    public static ChannelManager get() {
        return instance;
    }
    public void createOrUpdateChannel(CommonChannel channel) {

    }
    public CommonChannel getChannel(String id, byte type) {
        if(id.contains(tmpChannelPrefix)) {
            return tmpChannels.get(id+"-"+type);
        }
        if(type == CS.ChannelType.Person) {
            return getPersonChannel(id);
        }
        if(type == CS.ChannelType.Data) {
            return getOrCreateDataChannel(id,type);
        }
        return getChannelFromCacheOrStore(id,type);
    }
    //TODO 这里肯定是需要优化的，如果全部放在内存里，假设1000人，每人200个好友，就有2 0000 条数据，这是不能接受的，可以使用lRU算法优化
    public CommonChannel getPersonChannel(String fakeChannelID) {
        // in memory
        CommonChannel c = personChannels.get(fakeChannelID);
        if(c != null) {
            return c;
        }
        // need to load from store
        c = new CommonChannel();
        c.id = fakeChannelID;
        c.type = CS.ChannelType.Person;
        // c = store.getPersonChannel(fakeChannelID);
        personChannels.put(fakeChannelID, loadChannelData(c));
        return c;
    }

    // TODO  :  这里也是需要改的，不然dataChannel的意义呢？
    public CommonChannel getOrCreateDataChannel(String id, byte type) {
        String k = id+"-"+type;
        // in memory
        CommonChannel commonChannel = dataChannels.get(k);
        if(commonChannel != null) return commonChannel;
        // persitence store
        commonChannel = new CommonChannel();
        store.addDataChannel(id,type);
        // TODO  :  monitor layer
        // add in memory
        dataChannels.put(k, commonChannel);
        return commonChannel;
    }
    public CommonChannel getChannelFromCacheOrStore(String id, byte type) {
        String k = id+"-"+type;
        CommonChannel commonChannel = channels.get(k);
        if(commonChannel != null) return commonChannel;
        ChannelInfo info = store.getChannel(id, type);
        if (info == null) return null;
        commonChannel = new CommonChannel();
//        commonChannel.ban = info.ban;
//        commonChannel.large = info.large;
//        commonChannel.disband = info.disband;
        commonChannel.id = id;
        commonChannel.type = type;
        loadChannelData(commonChannel);
        channels.put(k,commonChannel);
        return commonChannel;
    }
    public CommonChannel loadChannelData(CommonChannel c) {
        // load subscribers
        List<String> subscribers = store.getSubscribers(c.id, c.type);
        if(subscribers != null && subscribers.size() > 0) {
            subscribers.forEach(c::addSubscriber);
        }
        //load deniedList
        List<String> deniedList = store.getDeniedList(c.id, c.type);
        if(deniedList != null && deniedList.size() > 0) {
            c.addBlockList(deniedList);
        }
        //load allowedList
        List<String> allowedList = store.getAllowedList(c.id, c.type);
        if(allowedList != null && allowedList.size() > 0) {
            c.addWhiteList(allowedList);
        }
        //load channel info
        ChannelInfo info = store.getChannel(c.id, c.type);
        if(info != null) {
//            c.ban = info.ban;
//            c.large = info.large;
//            c.disband = info.disband;
        }
        return c;
    }
    public void deleteChannelCache(String channelID,byte channelType) {
        String k = channelID+"-"+channelType;

//        if(channelType == CS.ChannelType.Person) {
//            personChannels.remove(k);
//            return;
//        }
        if(channelType == CS.ChannelType.Data) {
            dataChannels.remove(k);
            return;
        }
        channels.remove(k);
    }
    public void createTmpChannel(String channelID,byte channelType,List<String> subscribers) {
        String k = channelID+"-"+channelType;
        CommonChannel c = new CommonChannel();
        subscribers.forEach(c::addSubscriber);
        tmpChannels.put(k,c);
    }
    public boolean isTmpChannel(String channelID) {
        return channelID.contains(tmpChannelPrefix);
    }
}
