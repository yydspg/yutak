package com.yutak.im.proto;

import io.vertx.core.buffer.Buffer;

public class PingPacket extends Packet {
    @Override
    public byte getFrameType() {
        return CS.FrameType.PING;
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
