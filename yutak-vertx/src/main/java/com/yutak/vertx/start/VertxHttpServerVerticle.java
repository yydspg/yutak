package com.yutak.vertx.start;

import com.yutak.vertx.core.JSONMessageConvertor;
import com.yutak.vertx.core.SpringMvcRouterHandler;
import com.yutak.vertx.core.VertxHttpServerConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.Arrays;
import java.util.function.Consumer;

public class VertxHttpServerVerticle extends AbstractVerticle {

    private HttpServer httpServer;

    private Consumer<SpringMvcRouterHandler> after;
    private SpringMvcRouterHandler routerHandlerRegister;

    public VertxHttpServerVerticle(SpringMvcRouterHandler routerHandlerRegister, Consumer<SpringMvcRouterHandler> after) {
        this.after = after;
        this.routerHandlerRegister = routerHandlerRegister;
    }

    @Override
    public void start() throws Exception {
        HttpServer server = vertx.createHttpServer();
        this.httpServer = server;
        VertxHttpServerConfig serverConfig = routerHandlerRegister.getHttpServerConfig();
        routerHandlerRegister.routerHandle();
        after.accept(routerHandlerRegister);
        // default JSON convertor
        routerHandlerRegister.registerMessageConverter(new JSONMessageConvertor());
        Router router = serverConfig.getRouter();
        Arrays.stream(serverConfig.getStaticDir().split(",")).forEach(staticDir -> {
            router.route().handler(StaticHandler.create(staticDir));
        });
        this.httpServer.requestHandler(router::handle);
        this.httpServer.listen(serverConfig.getHttpPort());
    }
}
