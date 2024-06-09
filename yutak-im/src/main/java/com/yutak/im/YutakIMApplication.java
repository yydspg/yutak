package com.yutak.im;

import com.yutak.vertx.core.DefaultBeanFactory;
import com.yutak.vertx.core.VertxHttpServerConfig;
import com.yutak.vertx.start.ServerBoot;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.net.NetServer;

public class YutakIMApplication {
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
            System.out.println("server start");
        },s->{
            System.out.println("server start");
        });
    }
}
