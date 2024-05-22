package com.yutak.im.core;

import com.yutak.im.store.StoreApi;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Server {
    public Status status ;
    public Options options;
    public ConnectManager connectManager;
    public ConversationManager conversationManager;
    public DeliveryManager deliveryManager;
    public SystemUIDManager systemUIDManager;
    public boolean started;
    public long startTime = System.currentTimeMillis();
    public Map<String,Boolean> ipBlockList = new ConcurrentHashMap<>();
    private StoreApi store;

    public Server() {

    }
    public static class Status {
        public AtomicLong inboundMessages;
        public AtomicLong outboundMessages;
        public AtomicLong receivedBytes;
        public AtomicLong sendBytes;
        public AtomicLong slowClients;
    }
    public void addBlockIp(String ip) {
        ipBlockList.put(ip, true);
    }
    public void removeBlockIp(String ip) {
        ipBlockList.put(ip, false);
    }
    public void initIpBlockList(){
        List<String> blockList = store.getIpBlockList();
        if(blockList == null) return;
        for(String ip : blockList){
            ipBlockList.put(ip, true);
        }
    }
    public void removeIpBlockList(){
        ipBlockList.clear();
    }

    public void start(){

    }
    public void stop(){

    }
}
