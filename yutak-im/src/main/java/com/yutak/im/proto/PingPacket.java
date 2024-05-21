package com.yutak.im.proto;

import com.yutak.im.kit.BufferKit;
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
        PingPacket p = new PingPacket();
        BufferKit.decodeFixHeader(p,buffer.getByte(0));
        return p;
    }
}
