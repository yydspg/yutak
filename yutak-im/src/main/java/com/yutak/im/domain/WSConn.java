package com.yutak.im.domain;

import com.yutak.im.proto.ConnectPacket;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocket;

public class WSConn extends Conn{
    public ServerWebSocket webSocket;
    public WSConn(long id, String remoteAddr, ServerWebSocket webSocket, ConnectPacket connectPacket) {
        super(id,remoteAddr,connectPacket);
        this.webSocket = webSocket;
    }

    @Override
    public void close() {
        close.set(true);
        webSocket.close();
        packets.clear();
    }
}
