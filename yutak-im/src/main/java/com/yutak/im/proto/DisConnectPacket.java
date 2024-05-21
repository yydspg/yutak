package com.yutak.im.proto;

import io.vertx.core.buffer.Buffer;

public class DisConnectPacket extends Packet {

    public String reason;
    public byte reasonCode;
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
