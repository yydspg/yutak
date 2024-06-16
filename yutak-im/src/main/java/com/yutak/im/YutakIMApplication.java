package com.yutak.im;

import com.yutak.im.core.YutakNetServer;
import com.yutak.im.store.YutakStore;
import com.yutak.vertx.core.DefaultBeanFactory;
import com.yutak.vertx.core.VertxHttpServerConfig;
import com.yutak.vertx.start.ServerBoot;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.net.NetServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.EventRecordingLogger;


public class YutakIMApplication {
    private final static Logger log = LoggerFactory.getLogger(YutakIMApplication.class);
    public static void main(String[] args) {

        VertxHttpServerConfig vertxHttpServerConfig = new VertxHttpServerConfig();
        VertxOptions vertxOptions = new VertxOptions();
//        vertxOptions.setEventLoopPoolSize(1);
//        vertxOptions.setWorkerPoolSize(1);
        DefaultBeanFactory factory = new DefaultBeanFactory("");
        vertxHttpServerConfig.setBeanFactory(factory);
        vertxHttpServerConfig.setHttpPort(10001);
        vertxHttpServerConfig.setBasePackages("com.yutak.im");
        ServerBoot.start(vertxHttpServerConfig,h->{
            log.info("server start");
        },s->{
        });
        // add clean hock

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            YutakStore.get().destroy();
            YutakNetServer.get().destroy();
        }));
    }
}
