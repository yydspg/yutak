package com.yutak.im.handler;

import com.yutak.im.core.ConnectManager;
import com.yutak.im.core.Options;
import com.yutak.im.core.YutakNetServer;
import com.yutak.im.domain.Conn;
import com.yutak.im.kit.BufferKit;
import com.yutak.im.kit.SecurityKit;
import com.yutak.im.proto.*;
import com.yutak.im.store.Store;
import com.yutak.im.test.Test;
import com.yutak.vertx.kit.StringKit;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.VertxBuilder;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class YutakNetBuilder {
    private ConnectManager connectManager ;
    private PacketProcessor processor;
    private Vertx vertx;
    private Options options;
    private Store store;
    private final Logger log ;
    private final AtomicLong idGenerator = new AtomicLong(0);
    public YutakNetBuilder() {
        log = LoggerFactory.getLogger(YutakNetBuilder.class);
        vertx = Vertx.vertx();
        processor = PacketProcessor.get();
    }

    public Handler<NetSocket> netHandler() {
        return s -> {
            // ip block
            if (YutakNetServer.get().IPBlockList.get(s.remoteAddress().host()) != null) {
                s.end();
                return;
            }
            // statistics data inbound
//            s.handler(processor.statistics());
            // dispatch
            s.handler(processor.pipe(s));
        };
    }
    public static void main(String[] args) {
        Vertx vertx1 = Vertx.vertx();
        NetServer netServer = vertx1.createNetServer();
        YutakNetBuilder yutakNetBuilder = new YutakNetBuilder();
        // lambda 执行时,不会创建多此 handler
        YutakNetServer s = YutakNetServer.get();
        // Vertx 的 connectHandler 只能实现一个，分层的话需要在 connectHandler里对 Handler<Buffer> 分层

//        netServer.connectHandler(yutakNetBuilder.demoHandler());
        netServer.connectHandler(yutakNetBuilder.netHandler());
        netServer.connectHandler((socket)->{
            System.out.println("11");
            System.out.println(s.status.inboundMessages.getAndIncrement());
        });
        netServer.exceptionHandler(t->{
            System.out.println("error:["+t.getCause()+"]");
        });
        netServer.listen(9001)
                .onComplete(t->{
                   if(t.failed()) {
                       System.out.println("server start fail");
                   }
                   else System.out.println("server start success");
                });
        NetClient netClient = vertx1.createNetClient();
        ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(1);
        pool.scheduleAtFixedRate(()->{
            Future<NetSocket> future = netClient.connect(9001, "127.0.0.1").onComplete(t -> {
                if (t.succeeded()) System.out.println("client start success");
                else System.out.println("client start fail");
                NetSocket socket = t.result();
                ConnectPacket c = new ConnectPacket();
                c.UID = "cvasdffwefwe";
                c.clientTimestamp = System.currentTimeMillis();
                c.deviceID = "gradewefwef";
                c.token = "segvefewfw";
                c.deviceFlag = 1;
                c.clientKey = "cefcefwefAWEF";
                c.frameType = CS.FrameType.CONNECT;
                Buffer f = c.encode();
                socket.write(f);
//                socket.end();
                return;
            });
        },1,1, TimeUnit.SECONDS);

    }
    public  Handler<NetSocket> demoHandler() {
        return s -> {
//            System.out.println(s);
            s.handler(b->{
//                System.out.println(b);
                Vertx vertx1 = Vertx.vertx();
                vertx1.executeBlocking(p->{
                    p.fail("what can i say");
//                    p.complete(new ConnectPacket());
                }).onComplete(t->{
                    if(t.failed()) {
//                        System.out.println(t.cause().getMessage());
//                        System.out.println("fail");
                    }
                    if(t.succeeded()) {
//                        System.out.println("success");
                    }
                });
            });
        };
    }
}
