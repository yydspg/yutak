package com.yutak.im.proto;

import com.yutak.im.kit.BufferKit;
import io.vertx.core.buffer.Buffer;

public class SendAckPacket extends Packet{
    public long messageID;
    public int messageSeq;
    public long clientSeq;
    public byte reasonCode;
    public String clientMsgNo;
    @Override
    public int getFrameType() {
        return CS.FrameType.SENDACK;
    }

    @Override
    public Buffer encode() {
        Buffer b = Buffer.buffer();
        byte fixHeader = BufferKit.encodeFixHeader(this);
        b.appendByte(fixHeader)
                .appendLong(messageID)
                .appendInt(messageSeq)
                .appendLong(clientSeq)
                .appendByte(reasonCode)
                .appendString(clientMsgNo);
        return b;
    }

    @Override
    public Packet decode(Buffer b) {
        int i = 1,t = 9;
        messageID = b.getLong(i);
        i = t;
        t = i + 4;
        messageSeq = b.getInt(i);
        i = t;
        t = i + 8;
        clientSeq = b.getLong(i);
        i = t;
        reasonCode = b.getByte(i++);
        clientMsgNo = b.getString(i,b.length());
        return this;
    }

    public static void main(String[] args) {
        SendAckPacket s1 = new SendAckPacket();
        SendAckPacket s2 = new SendAckPacket();
        s1.messageID = 21314;
        s1.messageSeq = 21314;
        s1.clientSeq = 21314;
        s1.reasonCode = 0;
        s1.clientMsgNo = "fdaed";
        Buffer e = s1.encode();
        s2.decode(e);
        BufferKit.debug(s1,s2);
    }
}
