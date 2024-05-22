package com.yutak.im.proto;

import com.yutak.im.kit.BufferKit;
import io.vertx.core.buffer.Buffer;

public class ConnectPacket extends Packet {

    public String clientKey       ; // 客户端公钥
    public String deviceID        ; // 设备ID
    public byte deviceFlag      ; // 设备标示(同标示同账号互踢)
    public long clientTimestamp ; // 客户端当前时间戳(13位时间戳,到毫秒)
    public String UID             ; // 用户ID vachar(40)
    public String token           ; // token

    @Override
    public int getFrameType() {
        return 0;
    }

    @Override
    public Buffer encode() {
        Buffer b = Buffer.buffer();
        byte fixHeader = BufferKit.encodeFixHeader(this);
        b.appendByte(fixHeader)
            .appendByte(this.deviceFlag)
            .appendByte((byte) this.deviceID.length())
            .appendString(this.deviceID)
            .appendByte((byte) this.UID.length())
            .appendString(this.UID)
            .appendByte((byte) this.token.length())
            .appendString(this.token)
            .appendLong(this.clientTimestamp)
            .appendByte((byte) this.clientKey.length())
            .appendString(this.clientKey);
        return b;
    }

    @Override
    public Packet decode( Buffer buffer) {
        int i = 1,t = 0;
        this.deviceFlag = buffer.getByte(i++);
        t = i + buffer.getByte(i++) + 1;
        this.deviceID = buffer.getString(i,t);
        i = t ;
        t = i + buffer.getByte(i++) + 1;
        this.UID = buffer.getString(i,t);
        i = t ;
        t = i + buffer.getByte(i++) + 1;
        this.token = buffer.getString(i,t);
        i = t ;
        this.clientTimestamp = buffer.getLong(i);
        i +=8;
        t = i + buffer.getByte(i++) + 1;
        this.clientKey = buffer.getString(i,t);
        return this;
    }

    public static void main(String[] args) {
        ConnectPacket c = new ConnectPacket();
        c.UID = "cvasdffwefwe";
        c.clientTimestamp = System.currentTimeMillis();
        c.deviceID = "gradewefwef";
        c.token = "segvefewfw";
        c.deviceFlag = 1;
        c.clientKey = "cefcefwefAWEF";
        Buffer f = c.encode();
        ConnectPacket c1 = new ConnectPacket();
        c1.decode(f);
        BufferKit.debug(c1);
        BufferKit.debug(c1,c);
    }
}
