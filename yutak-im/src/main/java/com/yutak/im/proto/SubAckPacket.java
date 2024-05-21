package com.yutak.im.proto;

import io.vertx.core.buffer.Buffer;

public class SubAckPacket extends Packet{
    @Override
    public int getFrameType() {
        return 0;
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
