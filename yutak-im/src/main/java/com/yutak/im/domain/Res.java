package com.yutak.im.domain;

import com.yutak.im.store.H2Store;

import java.util.ArrayList;
import java.util.List;

public class Res {
    public static class OnlineConnect {
        public String uid;
        public byte deviceFlag;
    }
    public static class Msg {
        public Req.MessageHeader header;
        public byte setting;
        public long messageID;
        public String messageIDStr;
        public int streamSeq;
        public int streamFlag;
        public int messageSeq;
        public String topic;
        public int timeStamp;
        public String clientMsgNo;
        public String streamNo;
        public String fromUID;
        public String channelID;
        public byte channelType;
        public int expire;
        public byte[] payload;
        public List<StreamItem> streamItems;
        public Msg build(Message m) {
            setting = m.recvPacket.setting;
            header = new Req.MessageHeader();
            header.redDot = m.recvPacket.redDot;
            header.noPersist = m.recvPacket.noPersist;
            header.syncOnce = m.recvPacket.syncOnce;
            messageID = m.recvPacket.messageID;
            streamSeq = m.recvPacket.streamSeq;
            streamFlag = m.recvPacket.streamFlag;
            messageSeq = m.recvPacket.messageSeq;
            topic = m.recvPacket.topic;
            messageIDStr = String.valueOf(messageID);
            fromUID = m.recvPacket.fromUID;
            channelID = m.recvPacket.channelID;
            channelType = m.recvPacket.channelType;
            expire = m.recvPacket.expire;
            payload = m.recvPacket.payload;
            timeStamp = m.recvPacket.timestamp;
            clientMsgNo = m.recvPacket.clientMsgNo;
            streamNo = m.recvPacket.streamNo;
            streamItems = H2Store.get().getStreamItems(channelID, channelType, streamNo);
            return this;
        }
    }
    public static class StreamItem {
        public int streamSeq;
        public String clientMsgNo;
        public byte[] blob;
    }
}
