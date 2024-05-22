package com.yutak.im.proto;

import com.yutak.im.kit.BufferKit;
import io.vertx.core.buffer.Buffer;

public class SubPacket extends Packet {
    public byte setting;
    public String subNo;
    public String channelID;
    public byte channelType;
    public byte action;
    public String param;

    @Override
    public int getFrameType() {
        return CS.FrameType.SUB;
    }

    @Override
    public Buffer encode() {
        Buffer b = Buffer.buffer();
        byte fixHeader = BufferKit.encodeFixHeader(this);
        b.appendByte(fixHeader)
                .appendByte(setting)
                .appendByte((byte) subNo.length())
                .appendString(subNo)
                .appendByte((byte) channelID.length())
                .appendString(channelID)
                .appendByte(channelType)
                .appendByte(action)
                .appendByte((byte) param.length())
                .appendString(param);
        return b;
    }

    @Override
    public Packet decode( Buffer b) {
        int i = 1, t = 0;
        setting = b.getByte(i++);
        t = i + b.getByte(i++) + 1;
        subNo = b.getString(i,t);
        i = t;
        t = i + b.getByte(i++) + 1;
        channelID = b.getString(i,t);
        i = t;
        channelType = b.getByte(i++);
        action = b.getByte(i++);
        param = b.getString(i,b.length());
        return this;
    }

    public static void main(String[] args) {
        SubPacket s1 = new SubPacket();
        SubPacket s2 = new SubPacket();
        s1.subNo = "vbewigw";
        s1.action = 1;
        s1.channelID = "fewfw";
        s1.channelType = 2;
        s1.param = "wjrbvrbv";
        Buffer encode = s1.encode();
        s2.decode(encode);
        BufferKit.debug(s1,s2);
    }
}
