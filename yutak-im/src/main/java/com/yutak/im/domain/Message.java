package com.yutak.im.domain;

import com.yutak.im.proto.RecvPacket;
import io.vertx.core.buffer.Buffer;

import java.util.List;

public class Message {
    public RecvPacket recvPacket;
    public String toUID;                         // 接受者
    public List<String> subscribers;             // 订阅者
    public byte fromDeviceFlag;                  // 发送者设备标识
    public String fromDeviceID;                  // 发送者设备ID
    public byte large;
    public int index;
    public long pri;
    public int retryNum; // retry count
    public String toDeviceID;

    public byte[] encode(){
        return recvPacket.encode().getBytes();
    }
    // aim for storage
    public Message decode(byte[] b){
        RecvPacket r = new RecvPacket();
        Buffer buffer = Buffer.buffer(b);
        r.decode(buffer);
        recvPacket = r;
        return this;
    }
}
