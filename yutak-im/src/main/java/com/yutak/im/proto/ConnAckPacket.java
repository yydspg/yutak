package com.yutak.im.proto;

import com.yutak.im.kit.BufferKit;
import io.vertx.core.buffer.Buffer;

public class ConnAckPacket extends Packet {
    public byte serverVersion ; // 服务端版本
    public String serverKey     ; // 服务端的DH公钥
    public String salt          ; // salt
    public long timeDiff      ; // 客户端时间与服务器的差值，单位毫秒。
    public byte reasonCode    ; // 原因码
    public int nodeId        ; // 节点Id

    @Override
    public int getFrameType() {
        return CS.FrameType.CONNACK;
    }

    @Override
    public Buffer encode() {
        Buffer b = Buffer.buffer();
        byte fixHeader = BufferKit.encodeFixHeader(this);
        b.appendByte(fixHeader)
                .appendByte(serverVersion)
                .appendLong(timeDiff)
                .appendByte(reasonCode)
                .appendByte((byte) serverKey.length())
                .appendString(serverKey)
                .appendByte((byte) salt.length())
                .appendString(salt)
                .appendInt(nodeId);
        return b;
    }

    @Override
    public Packet decode( Buffer b) {
        int i = 1,t = 0;
        serverVersion = b.getByte(i++);
        timeDiff = b.getLong(i);
        i += 4;
        reasonCode = b.getByte(i++);
        t = i + b.getByte(i++) + 1;
        serverKey = b.getString(i,t);
        i = t;
        t = i + b.getByte(i++) + 1;
        salt = b.getString(i,t);
        i = t;
        nodeId = b.getInt(i);
        return this;
    }

    public static void main(String[] args) {
        ConnAckPacket c1 = new ConnAckPacket();
        ConnAckPacket c2 = new ConnAckPacket();
        c1.nodeId = 1322;
        c1.serverKey = "sefwefwefawegfwqg";
        c1.salt = "fwefqw4efgqw4g";
        c1.reasonCode = 0;
        c1.serverVersion = 1;
        Buffer encode = c1.encode();
        c2.decode(encode);
        BufferKit.debug(c1,c2);
    }
}
