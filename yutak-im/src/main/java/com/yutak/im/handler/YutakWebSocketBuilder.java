package com.yutak.im.handler;

import com.yutak.im.core.YutakNetServer;
import com.yutak.im.core.YutakSocket;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocket;
import io.vertx.core.net.NetServer;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class YutakWebSocketBuilder {
    private final PacketProcessor processor;
    private final static YutakWebSocketBuilder INSTANCE = new YutakWebSocketBuilder();
    public static YutakWebSocketBuilder getInstance() {
        return INSTANCE;
    }
    private YutakWebSocketBuilder() {
        processor = PacketProcessor.get();
    }
    public Handler<ServerWebSocket> processHandler() {
        return s->{
            // ip block
            if (YutakNetServer.get().IPBlockList.get(s.remoteAddress().host()) != null) {
                s.end();
                return;
            }
            // send to next layer packet process
            // aggregation socket process
            YutakSocket y = new YutakSocket(s);
            s.handler(processor.pipe(y));
            s.closeHandler(processor.close(y));
        };
    }
    public Handler<HttpServer> startSuccessHandler() {
        return n -> {
            log.debug("yutak ==> websocket server started successfully");
        };
    }
    public Handler<Throwable> startFailHandler() {
        return e -> {
            log.error(e.getMessage());
            log.error("yutak ==> net server failed to start");
        };
    }
    public Handler<Throwable> expectionHandler() {
        return e->{
            log.error(e.getMessage());
            // TODO  :  return client exception info
        };
    }
}
