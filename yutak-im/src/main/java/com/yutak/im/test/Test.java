package com.yutak.im.test;

import com.zaxxer.hikari.HikariConfig;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

        HikariConfig h = new HikariConfig();

        DataSource dataSource = h.getDataSource();

    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        NetServer netServer = vertx.createNetServer();
        netServer.connectHandler(s->{
            s.handler(b->{

                System.out.println(1);
                Demo demo = new Demo();
                demo.d1("hello",vertx).handle("world");
                demo.d2("hello1").handle("world1");
                Future.future(promise->{
                        promise.complete();
                        return;
                }).onComplete(res->{
                    System.out.println("sync ops");
                });
                demo.d3("hello2").handle("world2");
            });
            System.out.println(s.hashCode());
        });
        netServer.listen(8080);
        ScheduledThreadPoolExecutor s = new ScheduledThreadPoolExecutor(1);
        s.scheduleAtFixedRate(()->{
            NetClient netClient = vertx.createNetClient();
            netClient.connect(8080, "localhost", res -> {
                res.result().write("hello");
                res.result().end();
            });
        },1,1, TimeUnit.MILLISECONDS);

    }

}