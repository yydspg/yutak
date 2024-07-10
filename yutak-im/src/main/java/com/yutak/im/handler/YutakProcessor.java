package com.yutak.im.handler;


import com.yutak.im.core.ConnectManager;
import com.yutak.im.core.YutakSocket;
import com.yutak.im.kit.SocketKit;
import io.vertx.core.Handler;

public class YutakProcessor {

    public Handler<Void> close(YutakSocket s) {
        return  event->{
            String ip = s.remoteAddress.hostAddress();
            long l = SocketKit.ipToLong(ip);
            ConnectManager.get().removeConnect(l);
            s.close();
        };
    }
}
