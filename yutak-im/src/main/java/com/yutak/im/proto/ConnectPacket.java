package com.yutak.im.proto;

import io.vertx.core.buffer.Buffer;

public class ConnectPacket extends Packet {
    public int Version         ; // 协议版本
    public String ClientKey       ; // 客户端公钥
    public String DeviceID        ; // 设备ID
    public String DeviceFlag      ; // 设备标示(同标示同账号互踢)
    public long ClientTimestamp ; // 客户端当前时间戳(13位时间戳,到毫秒)
    public String UID             ; // 用户ID
    public String Token           ; // token

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
