package com.yutak.im.domain;

import com.yutak.im.proto.RecvPacket;

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
    public int retryNum;
    public String toDeviceID;
}
