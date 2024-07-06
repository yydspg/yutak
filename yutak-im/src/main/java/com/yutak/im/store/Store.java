package com.yutak.im.store;

import com.yutak.im.domain.*;

import java.util.List;

public interface Store {
    void open();
    void close();
    // user
    String getUserToken(String uid,byte deviceFlag);
    byte getUserDeviceLevel(String uid,byte deviceFlag);
    void updateUserToken(String uid,String token,byte deviceFlag,byte deviceLevel);
    void updateMessageOfUserCursorNeed(String uid,int messageSeq);
    // channel
    ChannelInfo getChannel(String channelID,byte channelType);
//    PersonChannel getPersonChannel(String channelID);
    void setPersonChannel(PersonChannel personChannel);
    void addOrUpdateChannel(ChannelInfo channelInfo);
    void addDataChannel(String channelID,byte channelType);
    boolean existChannel(String channelID,byte channelType);
    void addSubscribers(String channelID, byte channelType, List<String> subscribers);
    void removeSubscribers(String channelID, byte channelType, List<String> subscribers);
    List<String> getSubscribers(String channelID, byte channelType);
    void removeAllSubscribers(String channelID, byte channelType);
    List<String> getAllowedList(String channelID, byte channelType);
    List<String> getDeniedList(String channelID, byte channelType);
    void deleteChannel(String channelID,byte channelType);
    void addDeniedList(String channelID, byte channelType, List<String> deniedList);
    void removeDeniedList(String channelID, byte channelType, List<String> deniedList);
    void addAllowedList(String channelID, byte channelType, List<String> allowedList);
    void removeAllowedList(String channelID, byte channelType, List<String> allowedList);
    void removeAllAllowedList(String channelID, byte channelType);
    // message
    List<Integer> appendMessage(String channelID, byte channelType,List<Message> messages);
    List<Integer> appendMessageOfUser(String uid,List<Message> messages);
    Message loadMessage(String channelID,byte channelType,int seq);
    List<Message> loadLastMessages(String channelID,byte channelType,int limit);
    List<Message> loadLastMessagesWithEnd(String channelID,byte channelType,int limit,int end);
    List<Message> loadPrevRangeMsgs(String channelID,byte channelType,int limit,int start,int end);
    List<Message> loadNextRangeMsgs(String channelID,byte channelType,int limit,int start,int end);
    int getLastMessageSeq(String channelID,byte channelType);
    int getMessageOfUserCursor(String uid);
    List<Message> syncMessageOfUser(String uid,int startMsgSeq,int limit);
    void appendMessageOfNotifyQueue(List<Message> messages);
    List<Message> getMessagesOfNotifyQueue(int count);
    void removeMessageOfNotifyQueue(List<Long> messageIds);
    void deleteChannelAndClearMessage(String channelID,byte channelType);
    // conversations
    void addOrUpdateConversations(String uid,List<Conversation> conversations);
    List<Conversation> getConversations(String uid);
    Conversation getConversation(String channelID,byte channelType,String uid);
    void deleteConversation(String channelID,byte channelType,String uid);
    //system uids
    void addSystemUIDs(List<String> systemUIDs);
    void removeSystemUIDs(List<String> systemUIDs);
    List<String> getSystemUIDs();
    // message stream
//    void saveStreamMeta(Stream.Meta meta);
    void streamEnd(String channelID,byte channelType,String streamNo);
//    Stream.Meta getStreamMeta(String channelID,byte channelType,String streamNo);
//    int appendStreamItem(String channelID,byte channelType,String streamNo,Stream.Item item);
    List<Res.StreamItem> getStreamItems(String channelID, byte channelType, String streamNo);
    // ip
    void addIpBlockList(List<String> ips);
    void removeIpBlockList(List<String> ips);
    List<String> getIpBlockList();

}
