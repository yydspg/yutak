package com.yutak.im.proto;

import com.yutak.im.kit.BufferKit;
import io.vertx.core.buffer.Buffer;

public class RecvPacket extends Packet {
    public byte setting    ;
    public String msgKey     ;    // 用于验证此消息是否合法（仿中间人篡改）
    public int expire     ;    // 消息过期时间 0 表示永不过期
    public long messageID  ;    // 服务端的消息ID(全局唯一)
    public int messageSeq ;    // 消息序列号 （用户唯一，有序递增）
    public String clientMsgNo;    // 客户端唯一标示
    public String streamNo   ;    // 流式编号
    public int streamSeq  ;    // 流式序列号
    public byte streamFlag ; // 流式标记
    public int timestamp  ;    // 服务器消息时间戳(10位，到秒)
    public String channelID  ;    // 频道ID
    public byte channelType;    // 频道类型
    public String topic;    // 话题ID
    public String fromUID    ;    // 发送者UID
    public byte[] payload    ;    // 消息内容

    // ---------- 以下不参与编码 ------------
    public long ClientSeq ;// 客户端提供的序列号，在客户端内唯一
    @Override
    public int getFrameType() {
        return CS.FrameType.RECV;
    }

    @Override
    public Buffer encode() {
        Buffer b = Buffer.buffer();
        byte fixHeader = BufferKit.encodeFixHeader(this);
        b.appendByte(fixHeader)
                .appendByte(setting)
                .appendByte((byte) msgKey.length())
                .appendString(msgKey)
                .appendByte((byte) fromUID.length())
                .appendString(fromUID)
                .appendByte((byte) channelID.length())
                .appendString(channelID)
                .appendByte(channelType)
                .appendInt(expire)
                .appendByte((byte) clientMsgNo.length())
                .appendString(clientMsgNo)
                .appendByte((byte) streamNo.length())
                .appendString(streamNo)
                .appendInt(streamSeq)
                .appendByte(streamFlag)
                .appendLong(messageID)
                .appendInt(messageSeq)
                .appendInt(timestamp)
                .appendByte((byte) topic.length())
                .appendString(topic)
                .appendBytes(payload);
        return b;
    }

    @Override
    public Packet decode( Buffer b) {
        int i = 1,t = 0;
        setting = b.getByte(i++);
        t = i + b.getByte(i++) + 1;
        msgKey = b.getString(i,t);
        i = t;
        t = i + b.getByte(i++) + 1;
        fromUID = b.getString(i,t);
        i = t;
        t = i + b.getByte(i++) + 1;
        channelID = b.getString(i,t);
        i = t;
        channelType = b.getByte(i++);
        expire = b.getInt(i);
        i += 4;
        t = i + b.getByte(i++) + 1;
        clientMsgNo = b.getString(i,t);
        i = t;
        t = i + b.getByte(i++) + 1;
        streamNo = b.getString(i,t);
        i = t;
        streamSeq = b.getInt(i);
        i += 4;
        streamFlag = b.getByte(i++);
        messageID = b.getLong(i);
        i += 8;
        messageSeq = b.getInt(i);
        i += 4;
        timestamp = b.getInt(i);
        i += 4;
        t = i + b.getByte(i++) + 1;
        topic = b.getString(i,t);
        i = t;
        t = b.length();
        payload = b.getBytes(i,t);
        return this;
    }

    public static void main(String[] args) {
        RecvPacket r1 = new RecvPacket();
        r1.expire = 1;
        r1.setting = 0;
        r1.msgKey = "tbnvaoiervb";
        r1.fromUID = "tvboervuaw";
        r1.channelID = "tfboqweiufgp";
        r1.channelType = 121;
        r1.topic = "twefyqwye";
        r1.messageID = 112414;
        r1.messageSeq = 11234123;
        r1.timestamp = 1124124;
        r1.payload = "enov".getBytes();
        r1.clientMsgNo = "qbvierkyufqor";
        r1.streamNo = "test";
        r1.streamSeq = 1;
        r1.streamFlag = 1;
        Buffer encode = r1.encode();

        RecvPacket recvPacket = new RecvPacket();
        recvPacket.decode(encode);
        BufferKit.debug(r1,recvPacket);
    }
}
