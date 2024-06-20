package com.yutak.im.handler;


import com.yutak.im.core.ConnectManager;
import com.yutak.im.kit.SocketKit;
import com.yutak.vertx.kit.StringKit;
import io.vertx.core.Handler;
import io.vertx.core.net.NetSocket;

public class YutakProcessor {
    public static final YutakProcessor INSTANCE = new YutakProcessor();
    public YutakProcessor() {}
    public  static YutakProcessor getInstance(){
        return INSTANCE;
    }
    public Handler<Void> close(NetSocket s) {
        return  event->{
            String ip = s.remoteAddress().hostAddress();
            long l = SocketKit.ipToLong(ip);
            ConnectManager.get().removeConnect(l);
            s.close();
        };
    }
}
