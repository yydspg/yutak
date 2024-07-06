package com.yutak.im;

import com.yutak.im.core.TCPVerticle;
import com.yutak.im.core.YutakNetServer;
import com.yutak.im.handler.YutakWebSocketBuilder;
import com.yutak.im.store.YutakStore;
import com.yutak.vertx.core.DefaultBeanFactory;
import com.yutak.vertx.core.VertxHttpServerConfig;
import com.yutak.vertx.start.ServerBoot;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


public class YutakIMApplication {
    private final static Logger log = LoggerFactory.getLogger(YutakIMApplication.class);
    public static Vertx vertx;
    public static void main(String[] args) {

        VertxHttpServerConfig config = new VertxHttpServerConfig();
        VertxOptions o = new VertxOptions();
        o.setMaxWorkerExecuteTime(5);
        o.setMaxWorkerExecuteTimeUnit(TimeUnit.MILLISECONDS);
        o.setMaxEventLoopExecuteTime(1);
        o.setMaxEventLoopExecuteTimeUnit(TimeUnit.MILLISECONDS);
        Vertx ve = Vertx.vertx(o);
        vertx = ve;
        TCPVerticle tcpVerticle = new TCPVerticle();
        ve.deployVerticle(tcpVerticle);
        config.vertx = ve;
        config.beanFactory = new DefaultBeanFactory("");
        config.httpPort = 10001;
        config.basePackages  = "com.yutak.im";
        config.setServerSocketHandler(YutakWebSocketBuilder.getInstance().processHandler());
        ServerBoot.start(config,h->{
            log.info("Yutak IM Server started");
        },s->{

        });
        // add clean hock
        releaseMemory();
    }

    private static void releaseMemory() {
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            YutakStore.get().destroy();
            YutakNetServer.get().destroy();
        }));
    }
}
