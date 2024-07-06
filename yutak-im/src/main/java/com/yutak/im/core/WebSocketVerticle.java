package com.yutak.im.core;

import com.yutak.im.handler.YutakWebSocketBuilder;
import io.vertx.core.AbstractVerticle;

public class WebSocketVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.createHttpServer()
                .webSocketHandler(YutakWebSocketBuilder.getInstance().processHandler())
                .exceptionHandler(YutakWebSocketBuilder.getInstance().expectionHandler())
                .listen(Options.get().WSPort)
                .onComplete(YutakWebSocketBuilder.getInstance().startSuccessHandler(), YutakWebSocketBuilder.getInstance().startFailHandler());

    }
}
