package com.yutak.vertx.start;

import com.yutak.vertx.core.VertxMvcRouterHandler;
import com.yutak.vertx.core.VertxCS;
import com.yutak.vertx.core.VertxHttpServerConfig;
import com.yutak.vertx.kit.StringKit;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Consumer;
@Slf4j
public class ServerBoot {


    // config vertx,before means do something before server start ,after is same
    public static void start(VertxHttpServerConfig serverConfig, Consumer<VertxMvcRouterHandler> before, Consumer<VertxMvcRouterHandler> after) {
        // build server config
        resolveDefaultServerConfig(serverConfig);
        VertxMvcRouterHandler vertxMvcRouterHandler = new VertxMvcRouterHandler(serverConfig);
        VertxHttpServerVerticle vertxHttpServerVerticle = new VertxHttpServerVerticle(vertxMvcRouterHandler, after);
        before.accept(vertxMvcRouterHandler);
        serverConfig.getVertx().deployVerticle(vertxHttpServerVerticle).onComplete(t->{
            if (t.succeeded()) {
                log.info("yutak ==> Http verticle deployed");
            }
            else {
                log.error("yutak ==> Http verticle deploy failed", t.cause());
            }
        });
    }
    private static void resolveDefaultServerConfig(VertxHttpServerConfig serverConfig) {
        // set base package
        if (StringKit.isEmpty(serverConfig.basePackages)) {
            throw new VertxException("basePackages must not null");
        }
        if (Objects.isNull(serverConfig.beanFactory)) {
//            serverConfig.setBeanFactory(new SpringBeanFactory(serverConfig.getBasePackages()));
        }
        //set http port
        if (Objects.isNull(serverConfig.httpPort)) {
            serverConfig.setHttpPort(VertxCS.DEFAULT_SERVER_PORT);
        }
        // set bus connect timeout
        if (Objects.isNull(serverConfig.eventBusconnectTimeout)) {
            serverConfig.setEventBusconnectTimeout(VertxCS.DEFAULT_EVENTBUS_CONNECTTIMEOUT);
        }
        //set workPoolSize
        if (Objects.isNull(serverConfig.workPoolSize)) {
            serverConfig.setWorkPoolSize(Runtime.getRuntime().availableProcessors() * 2 + 1);
        }
        if (StringKit.isEmpty(serverConfig.staticDir)) {
            serverConfig.setStaticDir(VertxCS.DEALUT_STATIC_DIR);
        }
        // set vertx
        if (Objects.isNull(serverConfig.vertx)) {
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
        // set websocket server
        if (Objects.isNull(serverConfig.serverSocketHandler)){
            // default is auto close
            serverConfig.setServerSocketHandler(ServerWebSocket::close);
        }
    }


}
