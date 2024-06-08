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

}
