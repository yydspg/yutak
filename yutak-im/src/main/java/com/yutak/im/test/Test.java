package com.yutak.im.test;

import io.vertx.core.Handler;
import io.vertx.core.net.NetSocket;

/**
 * @author paul 2024/5/30
 */

public  class Test implements Handler<NetSocket> {
    private Test(){}

    private final static   Test  t = new Test() ;
    public static Test get() {
        return t;
    }
    @Override
    public void handle(NetSocket netSocket) {
        System.out.println("test");
        Class<? extends Test> aClass = this.getClass();
    }
}