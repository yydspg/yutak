package com.yutak.im.proto;

import com.yutak.im.kit.BufferKit;
import io.vertx.core.buffer.Buffer;
import lombok.SneakyThrows;

public class SendPacket extends Packet {
    public byte setting;
    public String msgKey     ; // 用于验证此消息是否合法（仿中间人篡改）
    public int expire     ; // 消息过期时间 0 表示永不过期
    public int clientSeq  ; // 客户端提供的序列号，在客户端内唯一
    public String clientMsgNo; // 客户端消息唯一编号一般是uuid，为了去重
    public String streamNo   ; // 流式编号
    public String channelID  ; // 频道ID（如果是个人频道ChannelId为个人的UID）
    public byte channelType; // 频道类型（1.个人 2.群组）
    public String topic      ; // 消息topic
    public byte[] payload    ; // 消息内容
    @Override
    public int getFrameType() {
        return CS.FrameType.SEND;
    }

    @Override
    public Buffer encode() {
        Buffer b = Buffer.buffer();
        byte fixHeader = BufferKit.encodeFixHeader(this);
        b.appendByte(fixHeader)
                .appendByte(setting)
                .appendInt(clientSeq)
                .appendByte((byte) clientMsgNo.length())
                .appendString(clientMsgNo)
                .appendByte((byte) streamNo.length())
                .appendString(streamNo)
                .appendByte((byte) channelID.length())
                .appendString(channelID)
                .appendByte(channelType)
                .appendInt(expire)
                .appendByte((byte) msgKey.length())
                .appendString(msgKey)
                .appendByte((byte) topic.length())
                .appendString(topic)
                .appendBytes(payload);
        return b;
    }

    @Override
    public Packet decode( Buffer buffer) {
        int i = 1,t = 0;
        this.setting = buffer.getByte(i++);
        this.clientSeq = buffer.getInt(i);
        i += 4;
        t = i + buffer.getByte(i++) + 1;
        this.clientMsgNo = buffer.getString(i,t);
        i = t;
        t = i + buffer.getByte(i++) + 1;
        this.streamNo = buffer.getString(i,t);
        i = t;
        t = i + buffer.getByte(i++) + 1;
        this.channelID = buffer.getString(i,t);
        i = t;
        this.channelType = buffer.getByte(i++);
        this.expire = buffer.getInt(i);
        i += 4;
        t = i + buffer.getByte(i++) + 1;
        this.msgKey = buffer.getString(i,t);
        i = t;
        t = i + buffer.getByte(i++) + 1;
        this.topic = buffer.getString(i,t);
        i = t;
        t = buffer.length();
        this.payload = buffer.getBytes(i,t);
        return this;
    }

    public static void main(String[] args) {
        SendPacket s1 = new SendPacket();
        s1.frameType = CS.FrameType.SEND;
        s1.setting = 1;
        s1.streamNo = "abcde";
        s1.clientMsgNo = "abcde";
        s1.clientSeq = 100;
        s1.channelID = "abcde";
        s1.channelType = 2;
        s1.expire = 100;
        s1.msgKey = "efgwgnbfsdbtyrf";
        s1.topic = "efgwfbdfbf";
        s1.payload = "efgwffweag3q4wg".getBytes();
        Buffer encode = s1.encode();

        SendPacket s2 = new SendPacket();
        s2.decode(encode);
//        Buffer encode1 = Protocol.encode(s1);
//        Packet decode = Protocol.decode(encode1);
//
//        BufferKit.debug(s1,decode);
    }
}
