package com.yutak.im.domain;

import io.vertx.core.net.NetSocket;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Conn {
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
    public NetSocket netSocket;

    public Conn(long id,String remoteAddr,NetSocket netSocket) {
        uid = "";
        this.id = id;
        this.remoteAddr = remoteAddr;
//        deviceLevel = 0;
//        deviceFlag = 0;
        upTime = LocalDateTime.now();
        lastActivity = LocalDateTime.now();
//        auth = false;
        close = new AtomicBoolean(false);
        this.netSocket = netSocket;
    }
    public void close() {
        close.set(true);
        netSocket.close();
    }
}
