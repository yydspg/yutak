package com.yutak.im.core;


import com.yutak.im.store.Store;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class YutakNetServer {
    public Status status ;
    public Options options;
    public ConnectManager connectManager;
    public ConversationManager conversationManager;
    public DeliveryManager deliveryManager;
    public SystemUIDManager systemUIDManager;
    public boolean started;
    public LocalDateTime startTime ;
    public Map<String,Boolean> IPBlockList ;
    private Store store;
    private NetServer netServer;
    public Vertx vertx;
    public AtomicLong ID; ;
    private final static YutakNetServer yutakNetServer;
    static {
        yutakNetServer = new YutakNetServer();
    }
    private YutakNetServer() {
        status = new Status();
        status.inboundMessages = new AtomicLong(0);
        connectManager = ConnectManager.get();
        IPBlockList = new ConcurrentHashMap<>();
        startTime = LocalDateTime.now();
        vertx = Vertx.vertx();
        ID = new AtomicLong(0);
    }

    public static YutakNetServer get() {
        return yutakNetServer;
    }

    public static class Status {
        public AtomicLong inboundMessages;
        public AtomicLong outboundMessages;
        public AtomicLong slowClients;
    }

    public void addBlockIp(String ip) {
        IPBlockList.put(ip, true);
    }
    public void removeBlockIp(String ip) {
        IPBlockList.put(ip, false);
    }
    public void initIpBlockList(){
        List<String> blockList = store.getIpBlockList();
        if(blockList == null) return;
        for(String ip : blockList){
            IPBlockList.put(ip, true);
        }
    }
    public void removeIpBlockList(){
        IPBlockList.clear();
    }

    public void start(){

    }
    public void stop(){

    }
}
