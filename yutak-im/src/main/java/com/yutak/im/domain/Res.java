package com.yutak.im.domain;

import java.time.LocalDateTime;
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
            return this;
        }
    }
    public static class Conversation {
        public String channelID; // Conversation channel
        public int channelType;
        public int unreadCount;    // Number of unread messages
        public long timestamp;  // Last session timestamp (10 digits)
        public Msg msg;
    }
    public static class StreamItem {
        public int streamSeq;
        public String clientMsgNo;
        public byte[] blob;
    }
    public static class ChannelRecentMsg {
        public String channelID;
        public int channelType;
        public List<Msg> msgs;
    }
    public static class Conn {
        public int total;       // total conn num
        public int offset;      // offset num
        public int limit;
        public String now;
        public ConnInfo connInfo;
    }
    public static class ConnInfo {
        public long ID;
        public String UID;
        public String IP;
        public int port;
        public String upTime;
        public String Idle;
        public int pendingBytes;
        public long inboundMsgs;
        public long outboundMsgs;
        public String device;
        public String deviceID;
        public int version;
        public LocalDateTime lastActivity;
    }
    public static class ChannelNum {
        public int tmpChannelNum;
        public int personChannelNum;
        public int commonChannelNum;
        public int dataChannelNum;
    }
 }
