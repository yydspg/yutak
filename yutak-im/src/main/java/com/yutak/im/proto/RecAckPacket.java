package com.yutak.im.proto;

import com.yutak.im.kit.BufferKit;
import io.vertx.core.buffer.Buffer;

public class RecAckPacket extends Packet {
    public long messageId;
    public int messageType;
    @Override
    public int getFrameType() {
        return CS.FrameType.RECVACK;
    }

    @Override
    public Buffer encode() {
        Buffer b = Buffer.buffer();
        byte fixHeader = BufferKit.encodeFixHeader(this);
        b.appendByte(fixHeader)
                .appendLong(messageId)
                .appendInt(messageType);
        return b;
    }

    @Override
    public Packet decode( Buffer buffer) {
        int i = 1;
        messageId = buffer.getLong(i);
        i += 8;
        messageType = buffer.getInt(i);
        return this;
    }

    public static void main(String[] args) {
        RecAckPacket recAckPacket = new RecAckPacket();
        recAckPacket.messageId = 2183123;
        recAckPacket.messageType = 2;
        Buffer encode = recAckPacket.encode();

        RecAckPacket packet = new RecAckPacket();
        packet.decode(encode);
        BufferKit.debug(recAckPacket,packet);
    }
}
