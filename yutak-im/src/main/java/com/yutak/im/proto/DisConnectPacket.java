package com.yutak.im.proto;

import com.yutak.im.kit.BufferKit;
import io.vertx.core.buffer.Buffer;

public class DisConnectPacket extends Packet {

    public String reason;
    public byte reasonCode;
    @Override
    public int getFrameType() {
        return CS.FrameType.DISCONNECT;
    }

    @Override
    public Buffer encode() {
        Buffer b = Buffer.buffer();
        byte fixHeader = BufferKit.encodeFixHeader(this);
        b.appendByte(fixHeader)
                .appendByte(reasonCode)
                .appendString(reason);
        return b;
    }

    @Override
    public Packet decode( Buffer buffer) {
        int i = 1;
        reasonCode = buffer.getByte(i++);
        reason = buffer.getString(i,buffer.length());
        return this;
    }

    public static void main(String[] args) {
        DisConnectPacket d1 = new DisConnectPacket();
        DisConnectPacket d2 = new DisConnectPacket();
        d1.reason ="fuewhlif";
        d1.reasonCode = 1;
        Buffer encode = d1.encode();
        d2.decode(encode);
        BufferKit.debug(d1,d2);
    }
}
