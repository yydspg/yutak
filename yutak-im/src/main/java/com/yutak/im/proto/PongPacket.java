package com.yutak.im.proto;

import com.yutak.im.kit.BufferKit;
import io.vertx.core.buffer.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PongPacket extends Packet {
    private static final Logger log = LoggerFactory.getLogger(PongPacket.class);

    @Override
    public int getFrameType() {
        return CS.FrameType.PONG;
    }

    @Override
    public Buffer encode() {
        Buffer buffer = Buffer.buffer();
        buffer.appendByte((byte) (getFrameType()<<4));
        return buffer;
    }

    @Override
    public Packet decode( Buffer buffer) {
        return this;
    }

    public static void main(String[] args) {
        byte a = 0x2a;
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(a);
        PingPacket pingPacket = new PingPacket();
        BufferKit.decodeFixHeader(pingPacket,buffer.getByte(0));

        log.info(pingPacket.toString());
        BufferKit.debug(pingPacket);
    }
}
