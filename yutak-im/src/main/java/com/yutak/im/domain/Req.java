package com.yutak.im.domain;

import com.yutak.im.store.Store;

import java.util.List;

public class Req {
    public static class ChannelCreate {
        public Store.ChannelInfo channelInfo;
        public List<String> subscribers;
    }
    public static class AddSubscriber {
        public String channelId;
        public List<String> subscribers;
        public int temp;
        public byte channelType;
        public int reset;
    }
    public static class RemoveSubscriber {
        public String channelId;
        public List<String> subscribers;
        public int temp;
        public byte channelType;
    }
    public static class BlockList {
        public String channelId;
        public List<String> uids;
        public byte channelType;
    }
    public static class WhiteList {
        public String channelId;
        public List<String> uids;
        public byte channelType;
    }
    public static class SendMessage {
        public MessageHeader header;
        public String clientMsgNo;
        public String streamNo;
        public String fromUID;
        public String channelID;
        public byte channelType;
        public int expire;
        public byte[] payload;
        public List<String> subscribers;
    }
    public static class MessageHeader {
        public boolean noPersist;
        public boolean redDot;
        public boolean syncOnce;
    }
    public static class DeviceQuit {
        public String uid;
        public byte deviceFlag;
    }
    public static class UpdateToken {
        public String uid;
        public String token;
        public byte deviceFlag;
        public byte deviceLevel;
    }
}
