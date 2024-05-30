package com.yutak.im.proto;

import io.vertx.core.buffer.Buffer;

public class PingPacket extends Packet {
    @Override
    public int getFrameType() {
        return CS.FrameType.PING;
    }

    @Override
    public Buffer encode() {
        Buffer buffer = Buffer.buffer();
        buffer.appendByte((byte) getFrameType());
        return buffer;
    }

    @Override
    public Packet decode( Buffer buffer) {
        return this;
    }
}
