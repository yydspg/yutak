package com.yutak.im.proto;

import io.vertx.core.buffer.Buffer;

public class RecAckPacket extends Packet {
    @Override
    public int getFrameType() {
        return CS.FrameType.RECVACK;
    }

    @Override
    public Buffer encode() {
        return null;
    }

    @Override
    public Packet decode( Buffer buffer) {
        return null;
    }
}
