package com.yutak.im.core;

import com.yutak.im.domain.CommonChannel;
import com.yutak.im.proto.CS;
import com.yutak.im.store.ChannelInfo;
import com.yutak.im.store.YutakStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

// 频道管理
public class ChannelManager {
    public final String tmpChannelPrefix = "tmp";
    private final YutakStore yutakStore;
    private final ConcurrentHashMap<String, CommonChannel> tmpChannels;
    // if subscribers == 0 ,remove from dataChannels
    private final ConcurrentHashMap<String, CommonChannel> dataChannels;
    private final LRU<String, CommonChannel> channels;
    private final LRU<String, CommonChannel> personChannels;
    private static ChannelManager instance;
    private final ThreadPoolExecutor executor;
    private Logger log = LoggerFactory.getLogger(ChannelManager.class);

    static {
        instance = new ChannelManager();
    }

    private ChannelManager() {
        this.channels = new LRU<>(1000);
        this.tmpChannels = new ConcurrentHashMap<>();
        this.dataChannels = new ConcurrentHashMap<>();
        this.personChannels = new LRU<>(1000);
        yutakStore = YutakStore.get();
        executor = new ThreadPoolExecutor(5, 8, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }

    public static ChannelManager get() {
        return instance;
    }

    public void destroy() {
        executor.shutdown();
        personChannels.clear();
        channels.clear();
        tmpChannels.clear();
        dataChannels.clear();
        log.info("yutak ==> ChannelManager destroyed");
    }

    public CommonChannel getChannel(String id, int type) {
        if (id.contains(tmpChannelPrefix)) {
            return tmpChannels.get(id + "-" + type);
        }
        if (type == CS.ChannelType.Person) {
            return getPersonChannel(id);
        }
        if (type == CS.ChannelType.Data) {
            return getOrCreateDataChannel(id, type);
        }
        return getChannelFromCache(id, type);
    }
    public CommonChannel getChannelSync(String id,int type) {
        if (getChannel(id, type) != null) {
            return getChannel(id, type);
        }
        // load from store
        // not in memory

        ChannelInfo info = yutakStore.getChannel(id, type);
        if (info == null) {
            // no such channel
            return null;
        }
        CommonChannel c = new CommonChannel();
        c.id = id;
        c.type = type;
        c.ban = info.ban;
        c.large = info.large;
        c.disband = info.disband;
        List<String> subscribers = yutakStore.getSubscribers(id, type);
        if (subscribers != null) {
            for (String subscriber : subscribers) {
                c.addSubscriber(subscriber);
            }
        }
        List<String> denyList = yutakStore.getDenyList(id, type);
        if (denyList != null) {
            c.addBlockList(denyList);
        }
        List<String> allowList = yutakStore.getAllowList(id, type);
        if (allowList != null) {
            c.addWhiteList(allowList);
        }

        // put into memory
        if (type == CS.ChannelType.Person) {
            personChannels.put(id, c);
        } else {
            channels.put(id + "-" + type, c);
        }
        return c;
    }
    private CommonChannel getPersonChannel(String fakeChannelID) {
        // in memory
        //todo something error,open im has no this issue
        return personChannels.get(fakeChannelID);
    }

    public CommonChannel getOrCreateDataChannel(String id, int type) {
        // in memory
        CommonChannel c = dataChannels.get(id + "-" + type);
        if (c == null) {
            c = new CommonChannel();
            dataChannels.put(id + "-" + type, c);
        }
        return dataChannels.get(id + "-" + type);
    }

    public CommonChannel getChannelFromCache(String id, int type) {
        return channels.get(id + "-" + type);
    }

    // this is the key api
    public CompletableFuture<CommonChannel> getChannelAsync(String id, int type) {
        return CompletableFuture.supplyAsync(() -> getOrCreateDataChannel(id, type), executor);
    }

    public void deleteChannelCache(String channelID, byte channelType) {
        String k = channelID + "-" + channelType;
        if (isTmpChannel(channelID)) {
            tmpChannels.remove(k);
            return;
        }
        if (channelType == CS.ChannelType.Data) {
            dataChannels.remove(k);
        }
    }

    public void removeDataChannel(String channelID, byte channelType) {
        if (channelType == CS.ChannelType.Data) {
            dataChannels.remove(channelID + "-" + channelType);
        }
    }

    public void removeTmpChannel(String channelID, byte channelType) {
        if (!isTmpChannel(channelID)) {
            return;
        }
        CommonChannel c = tmpChannels.remove(channelID + "-" + channelType);
        if (c != null && c.getSubscribedUsers().size() == 0) {
            tmpChannels.remove(channelID + "-" + channelType);
        }
    }

    public void createTmpChannel(String channelID, int channelType, List<String> subscribers) {
        String k = channelID + "-" + channelType;
        CommonChannel c = new CommonChannel();
        subscribers.forEach(c::addSubscriber);
        tmpChannels.put(k, c);
    }

    public boolean isTmpChannel(String channelID) {
        return channelID.contains(tmpChannelPrefix);
    }

    public int getTmpChannelNum() {
        return tmpChannels.size();
    }

    public int getDataChannelNum() {
        return dataChannels.size();
    }

    public int getChannelNum() {
        return channels.size();
    }
    public int getPersonChannelNum() {
        return personChannels.size();
    }
}
