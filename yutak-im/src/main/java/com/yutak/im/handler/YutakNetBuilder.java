package com.yutak.im.handler;

import com.yutak.im.core.Options;
import com.yutak.im.core.YutakNetServer;
import com.yutak.im.core.YutakSocket;
import com.yutak.im.proto.CS;
import com.yutak.im.proto.ConnectPacket;
import com.yutak.im.proto.PingPacket;
import com.yutak.im.store.Store;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class YutakNetBuilder {
    private PacketProcessor processor;
    private Options options;
    private Store store;
    private final Logger log ;
    private final static YutakNetBuilder yutakNetBuilder = new YutakNetBuilder();
    private YutakNetBuilder() {
        log = LoggerFactory.getLogger(YutakNetBuilder.class);
        processor = PacketProcessor.get();
    }
    public static YutakNetBuilder get() {
        return yutakNetBuilder;
    }
    public Handler<NetSocket> netHandler() {
        return s -> {
            // ip block
            if (YutakNetServer.get().IPBlockList.get(s.remoteAddress().host()) != null) {
                s.end();
                return;
            }
            // send to next layer packet process
            YutakSocket y = new YutakSocket(s);
            s.handler(processor.pipe(y));
            s.closeHandler(processor.close(y));
        };
    }
    public Handler<Throwable> exceptionHandler() {
        return e -> {
            log.error(e.getMessage());
            // TODO  :  data out process exception message to client
        };
    }
    public Handler<NetServer> startSuccessHandler() {
        return n -> {
          log.debug("yutak ==> net server started on port {}", n.actualPort());
        };
    }
    public Handler<Throwable> startFailHandler() {
        return e -> {
            log.error("yutak ==> net server failed to start ,cause {}", e.getMessage());
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
//        netServer.connectHandler((socket)->{
//            System.out.println("11");
//            System.out.println(s.status.inboundMessages.getAndIncrement());
//        });
        netServer.exceptionHandler(t->{
            System.out.println("error:["+t.getCause()+"]");
        });
        netServer.listen(10001)
                .onComplete(t->{
                   if(t.failed()) {
                       System.out.println("server start fail");
                   }
                   else System.out.println("server start success");
                });
        NetClient netClient = vertx1.createNetClient();
        ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(2);
        pool.scheduleAtFixedRate(()->{
            Future<NetSocket> future = netClient.connect(10001, "127.0.0.1").onComplete(t -> {
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
                PingPacket p = new PingPacket();
                p.frameType = CS.FrameType.PING;
                Buffer e = p.encode();
                Buffer f = c.encode();
                socket.write(f);
                socket.write(e);
                socket.end();
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
