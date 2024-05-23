package com.yutak.im.core;


import io.vertx.core.VertxOptions;
import io.vertx.core.net.NetServerOptions;

public class PacketPool {
    public static void main(String[] args) {
        NetServerOptions netServerOptions = new NetServerOptions();
        netServerOptions.setHost("127.0.0.1");
        netServerOptions.setPort(8080);
        netServerOptions.setTcpNoDelay(true);
        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setEventLoopPoolSize(1);
        vertxOptions.setEventLoopPoolSize(1);
        vertxOptions.setWorkerPoolSize(1);
        vertxOptions.setMaxEventLoopExecuteTime(500);
        vertxOptions.setMaxWorkerExecuteTime(500);
        vertxOptions.setMaxEventLoopExecuteTime(500);
    }
}
