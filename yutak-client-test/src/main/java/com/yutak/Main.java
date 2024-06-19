package com.yutak;

import com.yutak.client.ClientManager;
import com.yutak.im.kit.BufferKit;
import com.yutak.im.proto.CS;
import com.yutak.im.proto.ConnectPacket;
import com.yutak.im.proto.Packet;
import com.yutak.im.proto.RecvPacket;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        NetClient net = vertx.createNetClient();
        Future<NetSocket> f = net.connect(10002, "127.0.0.1");
        f.onComplete(ar -> {
            if (ar.succeeded()) {
                ConnectPacket c = new ConnectPacket();
                c.frameType = CS.FrameType.CONNECT;
                c.noPersist =1;
                c.redDot = 0;
                c.syncOnce = 0;
                c.UID = ClientManager.getUID();
                c.deviceID = "test";
                c.token = "demo";
                c.clientKey = "hello";
                NetSocket so = ar.result();
                so.write("hihgao");
                so.write(c.encode());
                System.out.println("connect success");
                    so.handler(b->{
                        Packet packet = BufferKit.decodePacket(b);
                        if (packet != null  ) {
                            if (packet.getFrameType() == CS.FrameType.RECV) {
                                RecvPacket r = (RecvPacket) packet;
                                System.out.println(String.valueOf(r.payload));
                            }
                        }
                    });
            }
        });
    }
}