package cn.yutak.vertx.start;

import cn.yutak.vertx.core.SpringMvcRouterHandler;
import cn.yutak.vertx.core.VertxHttpServerConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;

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

    }
}
