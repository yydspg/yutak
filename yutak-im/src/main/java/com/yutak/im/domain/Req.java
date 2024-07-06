package com.yutak.im.domain;

import com.yutak.im.store.ChannelInfo;
import io.vertx.core.json.Json;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Req {
    public static class ChannelCreate {
        public ChannelInfo channelInfo;
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
        public String payload;
        public List<String> subscribers;
    }
    public static class MessageHeader {
        public int noPersist;
        public int redDot;
        public int syncOnce;
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
    public static class QueryMessage {
        public String channelId;
        public byte channelType;
        public List<Integer> seqs;
    }
    public static class StreamStart {
        public Req.MessageHeader header;
        public String clientMsgNo;
        public String fromUID;
        public String channelID;
        public byte channelType;
        public String payload;
    }
    public static class StreamEnd {
        public String channelId;
        public byte channelType;
        public String streamNo;
    }
    public static class Sync {
        public String uid;
        public int messageSeq;
        public int limit;
    }
    public static class SyncAck{
        public String uid;
        public int lastMessageSeq;
    }
    public static class DeleteConversation extends Conversation {

    }
    public static class CleanConversationUnread extends Conversation {
        public int messageSeq;
    }
    public static class SetConversationUnread extends Conversation {
        public int messageSeq;
        public int unread;
    }
    public static class Conversation{
        public String uid;
        public String channelID;
        public int channelType;
    }
    public static class ChannelRecentMsg {
        public String channelID;
        public int channelType;
        public int lastMsgSeq;
    }
    public static class SyncRecentMsg {
        public String UID;
        public int msgCount;
        public List<ChannelRecentMsg> recentMsgs;
    }
    public static void main(String[] args) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.header = new MessageHeader();

        sendMessage.subscribers = new ArrayList<>();
        sendMessage.subscribers.add(":!233213");
        sendMessage.fromUID = "23";
        String s = new String("12312");
//        sendMessage.payload = s.getBytes(StandardCharsets.UTF_8);
        String encode = Json.encode(sendMessage);
        System.out.println(encode);
    }
}
