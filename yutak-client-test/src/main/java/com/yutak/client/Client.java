package com.yutak.client;

import com.yutak.im.proto.CS;
import com.yutak.im.proto.ConnectPacket;
import com.yutak.im.proto.RecvPacket;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Client {
    public String UID;
    public NetSocket socket;
    public void build() {
        NetClient netClient = ClientManager.vertx.createNetClient();
        netClient.connect(10002,"127.0.0.1").onComplete(s->{
            if (s.succeeded()){
                ConnectPacket c = new ConnectPacket();
                c.frameType = CS.FrameType.CONNECT;
                c.noPersist =1;
                c.redDot = 0;
                c.syncOnce = 0;
                c.UID = "3";
                c.deviceID = "test";
                c.token = "demo";
                c.clientKey = "hello";
                c.deviceFlag = 1;
                this.UID = c.UID;
//                Buffer buffer = c.encode();

//                ClientManager.put(this);
//                so.write(c.encode());
                NetSocket so = s.result();
//                NetSocket demo = so;
                socket = so;
                so.write(c.encode());
//                System.out.println(socket == so);
//                System.out.println(demo == so);
//                this.socket = s.result();
//                so.write("csudgvoaul");
//                demo.write("ni hoa");
//                so.write("csudgvoaul");
//                so.write("csudgvoaul");
//                so.write("csudgvoaul");
//
//                so.write("jihao");
//                log.debug("connect success,current num is {}",c.UID);
                int i = 0;
                so.handler(b->{
//                    System.out.println(this.toString()+"recv data");
                    RecvPacket r = new RecvPacket();
                    r.decode(b);
                    if(r != null) {
                        log.info(this.toString()+new String(r.payload));
//                        System.out.println(new String(r.payload));
                    }
                });
//                test(socket);
            }else {
                System.out.println("connect error");
            }
        });

    }
    private void test(NetSocket netSocket) {
        ScheduledThreadPoolExecutor s = new ScheduledThreadPoolExecutor(1);
        s.scheduleAtFixedRate(()->{
            netSocket.write("tesvasvar");
            System.out.println("start");
        },1,1,TimeUnit.SECONDS);

    }
    public static void main(String[] args) throws InterruptedException {
        Vertx vertx = ClientManager.vertx;
//        NetSocket n = null;
//        NetServer netServer = ClientManager.vertx.createNetServer();
//        netServer.connectHandler(s->{
//            System.out.println("build new connect");
////            vertx.setPeriodic(TimeUnit.SECONDS.toMillis(1), id -> {
////                s.write("Hello Server!\n");
////            });
//            s.handler(b->{
//                System.out.println("only one type"+b.getString(0,b.length()-1));
//            });
//        });
//        netServer.listen(8080);
        int i = 0;
            Client client = new Client();
            client.build();
            log.info(client.toString());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

//        TimeUnit.SECONDS.sleep(3);
    }
}
