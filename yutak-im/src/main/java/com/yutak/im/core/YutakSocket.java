package com.yutak.im.core;

import com.yutak.im.proto.CS;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;

public class YutakSocket {
    public int type;
    public SocketAddress remoteAddress;
    public NetSocket socket;
    public ServerWebSocket webSocket;
    public YutakSocket(NetSocket netSocket){
        socket = netSocket;
        remoteAddress = socket.remoteAddress();
    }
    public YutakSocket(ServerWebSocket webSocket){
        this.webSocket = webSocket;
        remoteAddress = socket.remoteAddress();
    }
    public void close(){
        if(type == CS.ConnType.tcp){
            socket.close();
            return;
        }
        if(type == CS.ConnType.websocket){
            webSocket.close();
            return;
        }
    }
    public void end(){
        if(type == CS.ConnType.tcp){
            socket.end();
            return;
        }
        if(type == CS.ConnType.websocket){
            webSocket.end();
            return;
        }
    }
    public void write(Buffer buffer){
        if(type == CS.ConnType.tcp){
            socket.write(buffer);
            return;
        }
        if(type == CS.ConnType.websocket){
            webSocket.write(buffer);
            return;
        }
    }
}
