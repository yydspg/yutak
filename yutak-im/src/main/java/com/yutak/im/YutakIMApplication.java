package com.yutak.im;

import com.yutak.im.core.YutakNetServer;
import com.yutak.im.handler.YutakNetBuilder;
import com.yutak.im.store.YutakStore;
import com.yutak.vertx.core.DefaultBeanFactory;
import com.yutak.vertx.core.VertxHttpServerConfig;
import com.yutak.vertx.start.ServerBoot;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.net.NetServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


public class YutakIMApplication {
    private final static Logger log = LoggerFactory.getLogger(YutakIMApplication.class);
    public static void main(String[] args) {

        VertxHttpServerConfig config = new VertxHttpServerConfig();
        VertxOptions o = new VertxOptions();
        o.setMaxWorkerExecuteTime(5);
        o.setMaxWorkerExecuteTimeUnit(TimeUnit.MILLISECONDS);
        o.setMaxEventLoopExecuteTime(1);
        o.setMaxEventLoopExecuteTimeUnit(TimeUnit.MILLISECONDS);
        Vertx ve = Vertx.vertx(o);
        NetServer netServer = ve.createNetServer();
        YutakNetBuilder yutakNetBuilder = new YutakNetBuilder();
        netServer.connectHandler(yutakNetBuilder.netHandler());

        netServer.listen(10002)
                .onComplete(t->{
                    if(t.failed()) {
                        System.out.println("tcp server start fail");
                    }
                    else log.info("tcp server start at post :{}",10002);
                });
        config.vertx = ve;
        DefaultBeanFactory factory = new DefaultBeanFactory("");
        config.beanFactory = factory;
        config.httpPort = 10001;
        config.basePackages  = "com.yutak.im";

        ServerBoot.start(config,h->{
            log.info(" http server start");
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
