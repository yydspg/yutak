package com.yutak.im.proto;

import com.yutak.im.kit.BufferKit;
import io.vertx.core.buffer.Buffer;

public class RecvPacket extends Packet {
    public int setting    ;
    public String msgKey     ;    // 用于验证此消息是否合法（仿中间人篡改）
    public int expire     ;    // 消息过期时间 0 表示永不过期
    public long messageID  ;    // 服务端的消息ID(全局唯一)
    public int messageSeq ;    // 消息序列号 （用户唯一，有序递增）
    public String clientMsgNo;    // 客户端唯一标示
    public String streamNo   ;    // 流式编号
    public int streamSeq  ;    // 流式序列号
    public String streamFlag ; // 流式标记
    public String timestamp  ;    // 服务器消息时间戳(10位，到秒)
    public String channelID  ;    // 频道ID
    public byte channelType;    // 频道类型
    public String topic;    // 话题ID
    public String fromUID    ;    // 发送者UID
    public byte[] payload    ;    // 消息内容

    // ---------- 以下不参与编码 ------------
    public long ClientSeq ;// 客户端提供的序列号，在客户端内唯一
    @Override
    public int getFrameType() {
        return 0;
    }

    @Override
    public Buffer encode() {
        return null;
    }

    @Override
    public Packet decode( Buffer b) {
        RecvPacket p = new RecvPacket();
        int i = 0;
        BufferKit.decodeFixHeader(p,b.getByte(i++));
        p.setting = b.getByte(1);
        return null;
    }

    public static void main(String[] args) {
        Buffer buffer = Buffer.buffer();
        int i = 0;
        buffer.appendString("csadcas");
    }
}
