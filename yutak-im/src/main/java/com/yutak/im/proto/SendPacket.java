package com.yutak.im.proto;

import io.vertx.core.buffer.Buffer;

public class SendPacket extends Packet {
   public String MsgKey     ; // 用于验证此消息是否合法（仿中间人篡改）
   public int Expire     ; // 消息过期时间 0 表示永不过期
   public long ClientSeq  ; // 客户端提供的序列号，在客户端内唯一
   public String ClientMsgNo; // 客户端消息唯一编号一般是uuid，为了去重
   public String StreamNo   ; // 流式编号
   public String ChannelID  ; // 频道ID（如果是个人频道ChannelId为个人的UID）
   public byte ChannelType; // 频道类型（1.个人 2.群组）
   public String Topic      ; // 消息topic
   public byte[] Payload    ; // 消息内容
    @Override
    public int getFrameType() {
        return 0;
    }

    @Override
    public Buffer encode() {
        return null;
    }

    @Override
    public Packet decode( Buffer buffer) {
        return null;
    }
}
