package com.yutak.im.proto;

import com.yutak.im.kit.BufferKit;
import io.vertx.core.buffer.Buffer;

public class SubAckPacket extends Packet{

    public String subNo;
    public String channelID;
    public byte channelType;
    public byte action;
    public byte reasonCode;
    @Override
    public int getFrameType() {
        return CS.FrameType.SUBACK;
    }

    @Override
    public Buffer encode() {
        Buffer b = Buffer.buffer();
        byte fixHeader = BufferKit.encodeFixHeader(this);
        b.appendByte(fixHeader)
                .appendByte((byte) subNo.length())
                .appendString(subNo)
                .appendByte((byte) channelID.length())
                .appendString(channelID)
                .appendByte(channelType)
                .appendByte(action)
                .appendByte(reasonCode);
        return b;
    }

    @Override
    public Packet decode( Buffer b) {
        int i = 1, t = 0;
        t = i + b.getByte(i++) + 1;
        subNo = b.getString(i,t);
        i = t;
        t = i + b.getByte(i++) + 1;
        channelID = b.getString(i,t);
        i = t;
        channelType = b.getByte(i++);
        action = b.getByte(i++);
        reasonCode = b.getByte(i++);
        return this;
    }

    public static void main(String[] args) {
        SubAckPacket s1 = new SubAckPacket();
        SubAckPacket s2 = new SubAckPacket();
        s1.subNo = "1rwegewar";
        s1.action = 1;
        s1.channelID = "fewfw";
        s1.channelType = 2;
        s1.reasonCode = 0;
        Buffer encode = s1.encode();
        s2.decode(encode);
        BufferKit.debug(s1,s2);
    }
}
