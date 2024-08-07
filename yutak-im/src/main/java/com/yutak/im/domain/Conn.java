package com.yutak.im.domain;

import com.yutak.im.core.ChannelManager;
import com.yutak.im.core.Options;
import com.yutak.im.proto.ConnectPacket;
import com.yutak.im.proto.Packet;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Conn {
    public String uid;
    public long id;
    public String remoteAddr;
    public byte deviceLevel;
    public byte deviceFlag;
    public String deviceID;
    public LocalDateTime upTime;
    public LocalDateTime lastActivity;
    public Duration maxIdle;
    public AtomicLong inMsgs;
    public AtomicLong outMsgs;
    public AtomicBoolean close;
    public boolean auth;
    public List<Packet> packets;
    private final ReentrantLock lock;
    private final ConcurrentHashMap<String, CommonChannel> channels;
    public Conn(long id, String remoteAddr, ConnectPacket connectPacket) {
        uid = connectPacket.UID;
        deviceFlag = connectPacket.deviceFlag;
        deviceID = connectPacket.deviceID;
        this.id = id;
        this.remoteAddr = remoteAddr;
//        deviceLevel = 0;
//        deviceFlag = 0;
        upTime = LocalDateTime.now();
        lastActivity = LocalDateTime.now();
//        auth = false;
        close = new AtomicBoolean(false);
        packets = new ArrayList<>();
        lock = new ReentrantLock();
        channels = new ConcurrentHashMap<>();
        outMsgs = new AtomicLong(0);
        maxIdle = Options.get().maxIdle;
    }
    public void add(Packet packet) {
        packets.add(packet);
    }
    public abstract void close();
    public void addPacket(Packet packet) {
        lock.lock();
        packets.add(packet);
        lock.unlock();
    }
    public void subscribe(String channelID,byte channelType) {
        CommonChannel c = ChannelManager.get().getChannel(channelID, channelType);
        if (c == null) {
            ChannelManager.get().getChannelAsync(channelID,channelType).whenComplete((r,e)->{
                if (e != null) {
                    LoggerFactory.getLogger(getClass()).error("subscribe error", e);
                }
                if( r != null) {
                    channels.put(channelID,c);
                }
            });
        }
    }
    public void unsubscribe(String channelID,byte channelType) {
        channels.remove(channelID+"-"+channelType);
    }
}
