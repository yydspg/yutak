package com.yutak.im.proto;

import io.vertx.core.buffer.Buffer;

public class RecvPacket extends Packet {
    public String Setting    ;
    public String MsgKey     ;    // 用于验证此消息是否合法（仿中间人篡改）
    public int Expire     ;    // 消息过期时间 0 表示永不过期
    public long MessageID  ;    // 服务端的消息ID(全局唯一)
    public int MessageSeq ;    // 消息序列号 （用户唯一，有序递增）
    public String ClientMsgNo;    // 客户端唯一标示
    public String StreamNo   ;    // 流式编号
    public int StreamSeq  ;    // 流式序列号
    public String StreamFlag ; // 流式标记
    public String Timestamp  ;    // 服务器消息时间戳(10位，到秒)
    public String ChannelID  ;    // 频道ID
    public byte ChannelType;    // 频道类型
    public String Topic      ;    // 话题ID
    public String FromUID    ;    // 发送者UID
    public byte[] Payload    ;    // 消息内容

    // ---------- 以下不参与编码 ------------
    public long ClientSeq ;// 客户端提供的序列号，在客户端内唯一
    @Override
    public byte getFrameType() {
        return 0;
    }

    @Override
    public Buffer encode(byte version) {
        return null;
    }

    @Override
    public Packet decode(byte version, Buffer buffer) {
        return null;
    }
}
