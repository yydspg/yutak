package com.yutak.im.core;

import com.yutak.im.handler.YutakNetBuilder;
import com.yutak.im.handler.YutakWebSocketBuilder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TCPVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.createNetServer().connectHandler(YutakNetBuilder.get().netHandler())
                .exceptionHandler(YutakNetBuilder.get().exceptionHandler())
                .listen(Options.get().TcpPort)
                .onComplete(YutakNetBuilder.get().startSuccessHandler(), YutakNetBuilder.get().startFailHandler());
    }
}
