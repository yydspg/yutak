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

        vertxOptions.setEventLoopPoolSize(5);
        vertxOptions.setWorkerPoolSize(5);
        vertxHttpServerConfig.setHttpPort(10002);
        vertxHttpServerConfig.setBasePackages("com.yutak.im");
        vertxHttpServerConfig.setBeanFactory(new DefaultBeanFactory("com.yutak.im"));

    }
}
