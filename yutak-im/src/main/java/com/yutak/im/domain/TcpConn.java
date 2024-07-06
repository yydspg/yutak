package com.yutak.im.domain;

import com.yutak.im.proto.ConnectPacket;
import io.vertx.core.http.WebSocket;
import io.vertx.core.net.NetSocket;

public class TcpConn extends Conn {
    public NetSocket netSocket;
    public TcpConn(long id, String remoteAddr, NetSocket netSocket, ConnectPacket connectPacket) {
        super(id,remoteAddr,connectPacket);
        this.netSocket = netSocket;
    }

    @Override
    public void close() {
        close.set(true);
        packets.clear();
        netSocket.close();
    }
}
