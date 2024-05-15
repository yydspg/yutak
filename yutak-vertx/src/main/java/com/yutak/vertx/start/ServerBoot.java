package com.yutak.vertx.start;

import com.yutak.vertx.core.SpringBeanFactory;
import com.yutak.vertx.core.SpringMvcRouterHandler;
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
        private static final Logger log = LoggerFactory.getLogger(ServerBoot.class);
    public static void start(String basepackages, Integer httpPort, Consumer<SpringMvcRouterHandler> before, Consumer<SpringMvcRouterHandler> after) {
        VertxHttpServerConfig serverConfig = new VertxHttpServerConfig();

        serverConfig.setHttpPort(httpPort);
        serverConfig.setBasePackages(basepackages);
        start(serverConfig, before, after);
    }

    public static void start(VertxHttpServerConfig serverConfig, Consumer<SpringMvcRouterHandler> before, Consumer<SpringMvcRouterHandler> after) {
        resolveDefaultServerConfig(serverConfig);
        SpringMvcRouterHandler springMvcRouterHandler = new SpringMvcRouterHandler(serverConfig);
        VertxHttpServerVerticle vertxHttpServerVerticle = new VertxHttpServerVerticle(springMvcRouterHandler, after);
        before.accept(springMvcRouterHandler);
        serverConfig.getVertx().deployVerticle(vertxHttpServerVerticle);
    }
    private static void resolveDefaultServerConfig(VertxHttpServerConfig serverConfig) {
        if (StringKit.isEmpty(serverConfig.getBasePackages())) {
            throw new VertxException("basePackages must not null");
        }
        if (Objects.isNull(serverConfig.getBeanFactory())) {
//            serverConfig.setBeanFactory(new SpringBeanFactory(serverConfig.getBasePackages()));
        }
        if (Objects.isNull(serverConfig.getHttpPort())) {
            serverConfig.setHttpPort(VertxCS.DEFAULT_SERVER_PORT);
        }
        if (Objects.isNull(serverConfig.getEventBusconnectTimeout())) {
            serverConfig.setEventBusconnectTimeout(VertxCS.DEFAULT_EVENTBUS_CONNECTTIMEOUT);
        }
        if (Objects.isNull(serverConfig.getWorkPoolSize())) {
            serverConfig.setWorkPoolSize(Runtime.getRuntime().availableProcessors() * 2 + 1);
        }
        if (StringKit.isEmpty(serverConfig.getStaticDir())) {
            serverConfig.setStaticDir(VertxCS.DEALUT_STATIC_DIR);
        }
        if (Objects.isNull(serverConfig.getVertx())) {
            EventBusOptions eventBusOptions = new EventBusOptions();
            eventBusOptions.setConnectTimeout(serverConfig.getEventBusconnectTimeout());
            Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(serverConfig.getWorkPoolSize())
                    .setEventBusOptions(eventBusOptions));
            serverConfig.setVertx(vertx);
        }
        if (Objects.isNull(serverConfig.getRouter())) {
            serverConfig.setRouter(Router.router(serverConfig.getVertx()));
        }

        if (StringKit.isEmpty(serverConfig.getStaticDir())) {
            serverConfig.setStaticDir(VertxCS.DEALUT_STATIC_DIR);
        }

    }


}
