package com.yutak.im.core;


import com.yutak.im.store.YutakStore;
import io.vertx.core.net.NetServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class YutakNetServer {
    public Status status ;
    public Options options;
    public ConnectManager connectManager;
    public ConversationManager conversationManager;
    public ChannelManager channelManager;
    public DeliveryManager deliveryManager;
    public SystemUIDManager systemUIDManager;
    public boolean started;
    public LocalDateTime startTime ;
    public Map<String,Boolean> IPBlockList ;
    private final YutakStore yutakStore;
    private NetServer netServer;
    public AtomicLong ID; ;
    private final static YutakNetServer yutakNetServer;
    private final Logger log = LoggerFactory.getLogger(YutakNetServer.class);
    static {
        yutakNetServer = new YutakNetServer();
    }
    private YutakNetServer() {
        status = new Status();
        status.inboundMessages = new AtomicLong(0);
        status.outboundMessages = new AtomicLong(0);
        connectManager = ConnectManager.get();
        IPBlockList = new ConcurrentHashMap<>();
        startTime = LocalDateTime.now();
        ID = new AtomicLong(0);
        yutakStore = YutakStore.get();
        channelManager = ChannelManager.get();
        systemUIDManager = SystemUIDManager.get();
    }
    public void destroy() {
        log.info("yutak ==> server try to stop");
        connectManager.destroy();
        channelManager.destroy();
        systemUIDManager.destroy();
        log.info("yutak ==> server stopped");
    }
    public static YutakNetServer get() {
        return yutakNetServer;
    }

    public static class Status {
        public AtomicLong inboundMessages;
        public AtomicLong outboundMessages;
        public AtomicLong slowClients;
    }

    public void addBlockIp(List<String> ip) {
        if( ip.isEmpty()) {
            return;
        }
        Set<String> set = IPBlockList.keySet();
        List<String> realIPs = new ArrayList<>();
        ip.forEach(t->{
            if(!set.contains(t)) {
                realIPs.add(t);
                IPBlockList.put(t, true);
            }
        });
        yutakStore.addIPBlockList(realIPs);
    }
    public void removeBlockIp(List<String> ips) {
        if(ips == null || ips.isEmpty()) {
            return;
        }
        Set<String> set = IPBlockList.keySet();
        List<String> realIPs = new ArrayList<>();
        ips.forEach(t->{
            if(set.contains(t)) {
                realIPs.add(t);
                IPBlockList.remove(t);
            }
        });
        yutakStore.removeIPBlockList(realIPs);
    }
    public void initIpBlockList(){
        yutakStore.getIPBlockList().whenComplete((r,e)->{
            if(e != null) {
                log.error("get Ip blockList error"+e.getMessage());
                return;
            }
            if(r != null) {
                r.forEach(t->{
                    IPBlockList.put(t,true);
                });
            }
        });
    }
    public void removeIpBlockList(){
        IPBlockList.clear();
    }


}
