package com.yutak.im.domain;

import com.yutak.im.core.DeliveryManager;
import com.yutak.im.proto.CS;
import com.yutak.im.proto.RecvPacket;
import com.yutak.im.store.YutakStore;
import com.yutak.vertx.kit.StringKit;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommonChannel extends Channel {
    public String id;
    public byte type;
    // 0 means false ,1 means true
    public byte ban;
    public byte large;
    public byte disband;
    private Map<String,Boolean> blockList;
    private Map<String,Boolean> whiteList;
    private Map<String,Boolean> subscribers;
    private Map<String,Boolean> tmpSubscribers;

    public CommonChannel() {
        blockList = new ConcurrentHashMap<>();
        whiteList = new ConcurrentHashMap<>();
        subscribers = new ConcurrentHashMap<>();
        tmpSubscribers = new ConcurrentHashMap<>();
    }
    // return true means add correct
    // return false means subscriber already success or s is null
    public boolean addSubscriber(String s) {
       if(s == null) return false;
       if(subscribers.containsKey(s)) return false;
       subscribers.put(s, true);
       return true;
    }
    public void addBlockList(List<String> list) {
        list.forEach(s -> blockList.put(s, true));
    }
    public void addWhiteList(List<String> list) {
        list.forEach(s -> whiteList.put(s, true));
    }

    @Override
    public boolean baned() {
        return ban == 1;
    }

    @Override
    public List<String> getSubscribedUsers() {
        return subscribers.keySet().stream().toList();
    }
    public void addTmpSubscriber(List<String> tmp) {
        if (tmp != null && !tmp.isEmpty()) {
            ConcurrentHashMap<String, Boolean> tmpMap = new ConcurrentHashMap<>();
            tmp.forEach(t->{tmpMap.put(t,true);});
            tmpSubscribers.putAll(tmpMap);
        }
    }
    public void removeAllTmpSubscriber() {
        tmpSubscribers.clear();
    }
    public void removeAllSubscriber() {
        subscribers.clear();
    }
    public void removeSubscribers(List<String> subscribers) {
        subscribers.forEach(t->{tmpSubscribers.remove(t);});
    }
    public void removeBlockList(List<String> list) {
        list.forEach(t->{blockList.remove(t);});
    }
    public void removeWhiteList(List<String> list) {
        list.forEach(t->{whiteList.remove(t);});
    }
    public void removeAllBlockList() {
        blockList.clear();
    }
    public void removeAllWhiteList() {
        whiteList.clear();
    }
    public List<String> getTmpSubscribers() {
        return tmpSubscribers.keySet().stream().toList();
    }
    public List<String> getWhiteList() {
        return whiteList.keySet().stream().toList();
    }
    public List<String> getBlockList() {
        return blockList.keySet().stream().toList();
    }
    // put message to current channel
    public Handler<Promise<Map<String,Integer>>> putMessage(List<Message> msgs,List<String> customSubscribers,String fromUID,String fromDeviceUID,int fromDeviceFlag) {
        return promise -> {
            if (msgs == null || msgs.isEmpty()) {
                promise.fail("msgs is null or empty");
                return;
            }
            List<String> list = new ArrayList<>();
            // put real subscribers
            if (customSubscribers != null && !customSubscribers.isEmpty()) {
                list.addAll(customSubscribers);
            } else {
                list.addAll(subscribers.keySet());
            }
            System.out.println("subscribers size:"+list.size());
            if(list.size() == 0) {
                promise.fail("no subscribers");
                return;
            }
            // store message in user queue
            Map<String, Integer> map = storeMessageIfNeed(msgs, list);
            // update conversation

            // return
            // start delivery message
            DeliveryManager.get().routeMsg(msgs,list,map,fromUID,fromDeviceUID,fromDeviceFlag);
            promise.complete(map);
        };
    }
    private Map<String,Integer> storeMessageIfNeed(List<Message> msgs, List<String> subscribers) {
        if (subscribers == null || subscribers.isEmpty()) {
            return null ;
        }
        Map<String, Integer> map = new HashMap<>();
        List<Message> storeMsgs = new ArrayList<>();
        List<Message> persistMsg = msgs.stream().filter(m -> m.recvPacket.noPersist == 0 || m.recvPacket.syncOnce == 0).toList();
        for (String s : subscribers) {
            for (Message m : persistMsg) {
                Message tmp = deepCopy(m);
                // message to
                tmp.toUID = s;
                tmp.large = large;
                if (tmp.recvPacket.channelType == CS.ChannelType.Person && StringKit.same(m.recvPacket.channelID, s)) {
                    // message from
                    tmp.recvPacket.channelID = m.recvPacket.fromUID;
                }
                storeMsgs.add(tmp);
            }
            // store message for every subscriber
            if (storeMsgs.size() > 0) {
                // yutak store
                YutakStore.get().appendMessageOfUser(s, storeMsgs);
                storeMsgs.forEach(ss->map.put(s+"-"+ss.toUID,ss.recvPacket.messageSeq));
            }
        }
        return map;
    }
    public Message deepCopy(Message msg) {
        Message m = new Message();
        RecvPacket r = new RecvPacket();
        m.large = msg.large;
        m.toUID = msg.toUID;
        r.channelType = msg.recvPacket.channelType;
        r.expire  = msg.recvPacket.expire;
        r.redDot = msg.recvPacket.redDot;
        r.setting = msg.recvPacket.setting;
        r.streamNo = msg.recvPacket.streamNo;
        r.syncOnce = msg.recvPacket.syncOnce;
        r.timestamp = msg.recvPacket.timestamp;
        r.payload = msg.recvPacket.payload;
        r.clientMsgNo = msg.recvPacket.clientMsgNo;
        r.frameType = msg.recvPacket.frameType;
        r.msgKey = msg.recvPacket.msgKey;
        r.topic = msg.recvPacket.topic;
        r.ClientSeq = msg.recvPacket.ClientSeq;
        r.hasServerVersion = msg.recvPacket.hasServerVersion;
        r.messageSeq = msg.recvPacket.messageSeq;
        r.dup = msg.recvPacket.dup;
        r.streamSeq = msg.recvPacket.streamSeq;
        m.recvPacket = r;
        return m;
    }
    public static void main(String[] args) {
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
        map.put("id","1");
        System.out.println(map.get("id"));
        System.out.println(map.containsKey("id"));
    }
}
