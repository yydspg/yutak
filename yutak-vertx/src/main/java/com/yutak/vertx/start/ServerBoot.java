package com.yutak.vertx.start;

import com.yutak.vertx.core.VertxMvcRouterHandler;
import com.yutak.vertx.core.VertxCS;
import com.yutak.vertx.core.VertxHttpServerConfig;
import com.yutak.vertx.kit.StringKit;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Consumer;

public class ServerBoot {

    public static void start(String basepackages, Integer httpPort, Consumer<VertxMvcRouterHandler> before, Consumer<VertxMvcRouterHandler> after) {
        VertxHttpServerConfig serverConfig = new VertxHttpServerConfig();

        serverConfig.setHttpPort(httpPort);
        serverConfig.setBasePackages(basepackages);
        start(serverConfig, before, after);
    }
    // config vertx
    public static void start(VertxHttpServerConfig serverConfig, Consumer<VertxMvcRouterHandler> before, Consumer<VertxMvcRouterHandler> after) {
        resolveDefaultServerConfig(serverConfig);
        VertxMvcRouterHandler vertxMvcRouterHandler = new VertxMvcRouterHandler(serverConfig);
        VertxHttpServerVerticle vertxHttpServerVerticle = new VertxHttpServerVerticle(vertxMvcRouterHandler, after);
        before.accept(vertxMvcRouterHandler);
        serverConfig.getVertx().deployVerticle(vertxHttpServerVerticle);
    }
    private static void resolveDefaultServerConfig(VertxHttpServerConfig serverConfig) {
        // set base package
        if (StringKit.isEmpty(serverConfig.getBasePackages())) {
            throw new VertxException("basePackages must not null");
        }
        if (Objects.isNull(serverConfig.getBeanFactory())) {
//            serverConfig.setBeanFactory(new SpringBeanFactory(serverConfig.getBasePackages()));
        }
        //set http port
        if (Objects.isNull(serverConfig.getHttpPort())) {
            serverConfig.setHttpPort(VertxCS.DEFAULT_SERVER_PORT);
        }
        // set bus connect timeout
        if (Objects.isNull(serverConfig.getEventBusconnectTimeout())) {
            serverConfig.setEventBusconnectTimeout(VertxCS.DEFAULT_EVENTBUS_CONNECTTIMEOUT);
        }
        //set workPoolSize
        if (Objects.isNull(serverConfig.getWorkPoolSize())) {
            serverConfig.setWorkPoolSize(Runtime.getRuntime().availableProcessors() * 2 + 1);
        }
        if (StringKit.isEmpty(serverConfig.getStaticDir())) {
            serverConfig.setStaticDir(VertxCS.DEALUT_STATIC_DIR);
        }
        // set vertx
        if (Objects.isNull(serverConfig.getVertx())) {
            EventBusOptions eventBusOptions = new EventBusOptions();
            eventBusOptions.setConnectTimeout(serverConfig.getEventBusconnectTimeout());
            // vertx Options ! this is important
            Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(serverConfig.getWorkPoolSize())
                    .setEventBusOptions(eventBusOptions));
            serverConfig.setVertx(vertx);
        }
        // set router
        if (Objects.isNull(serverConfig.getRouter())) {
            serverConfig.setRouter(Router.router(serverConfig.getVertx()));
        }
    }


}
