package com.yutak.im.proto;

import io.vertx.core.buffer.Buffer;

public class ConnAckPacket extends Packet {
    public byte ServerVersion ; // 服务端版本
    public String ServerKey     ; // 服务端的DH公钥
    public String Salt          ; // salt
    public long TimeDiff      ; // 客户端时间与服务器的差值，单位毫秒。
    public String ReasonCode    ; // 原因码
    public int NodeId        ; // 节点Id

    @Override
    public byte getFrameType() {
        return 0;
    }

    @Override
    public Buffer encode(byte version) {
        return null;
    }

    @Override
    public Packet decode(byte version, Buffer buffer) {
        return null;
    }
}
