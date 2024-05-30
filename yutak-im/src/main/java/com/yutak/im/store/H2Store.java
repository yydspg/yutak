package com.yutak.im.store;

import com.yutak.im.domain.Conversation;
import com.yutak.im.domain.Message;
import com.yutak.im.domain.Stream;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Setter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

public class H2Store implements Store {
    public String userTokenPrefix        ;
    public String channelPrefix          ;
    public String subscribersPrefix      ;
    public String denylistPrefix         ;
    public String allowlistPrefix        ;
    public String notifyQueuePrefix      ;
    public String userSeqPrefix          ;
    public String nodeInFlightDataPrefix ;
    public String systemUIDsKey          ;
    public String ipBlacklistKey         ;
    public HikariDataSource hikariDataSource;
    public Logger log;

    public H2Store() {
        HikariConfig c = new HikariConfig();
        c.setDriverClassName("org.h2.Driver");
        c.setMaximumPoolSize(20);
        c.setMinimumIdle(2);
        c.setAutoCommit(true);
        c.setPoolName("h2");
        c.setJdbcUrl("jdbc:h2:/home/paul/data/yutak");
        c.setUsername("paul");
        c.setPassword("1234");
        hikariDataSource = new HikariDataSource(c);
        log = LoggerFactory.getLogger(H2Store.class);
    }

    @SneakyThrows
    public Statement getStatement() {
        return hikariDataSource.getConnection().createStatement();
    }
    @SneakyThrows
    public ResultSet query(String sql) {
        return getStatement().executeQuery(sql);
    }
    @SneakyThrows
    public void update(String sql) {
        getStatement().executeUpdate(sql);
    }
    public static void main(String[] args) throws SQLException {
        H2Store h2Store = new H2Store();
        h2Store.getUserToken("123", (byte) 0);
        h2Store.updateUserToken("123","feasfe",(byte) 0,(byte)2);
    }

    @Override
    public void open() {

    }

    @Override
    public void close() {

    }


    @Override
    public String getUserToken(String uid, byte deviceFlag) {
        // TODO  : 存在  sql 注入风险
        ResultSet set = query("SELECT TOKEN FROM USER_TOKEN WHERE UID = '" + uid  + "'");
        String token = "";
        try {
            token = set.getString(2);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return token;
    }

    @Override
    public byte getUserDeviceLevel(String uid, byte deviceFlag) {
        return 0;
    }

    @Override
    public void updateUserToken(String uid, String token, byte deviceFlag, byte deviceLevel) {
        System.out.println("test updateUserToken");
        update("update user_token set token = '" + token + ", device_level ="+ deviceLevel + "' where uid = '" + uid + deviceFlag + "'");
    }

    @Override
    public void updateMessageOfUserCursorNeed(String uid, int messageSeq) {

    }

    @Override
    public ChannelInfo getChannel(String channelID, byte channelType) {
        return null;
    }

    @Override
    public void addOrUpdateChannel(ChannelInfo channelInfo) {

    }

    @Override
    public boolean existChannel(String channelID, byte channelType) {
        return false;
    }

    @Override
    public void addSubscribers(String channelID, byte channelType, List<String> subscribers) {

    }

    @Override
    public void removeSubscribers(String channelID, byte channelType, List<String> subscribers) {

    }

    @Override
    public List<String> getSubscribers(String channelID, byte channelType) {
        return List.of();
    }

    @Override
    public void removeAllSubscribers(String channelID, byte channelType) {

    }

    @Override
    public List<String> getAllowedList(String channelID, byte channelType) {
        return List.of();
    }

    @Override
    public List<String> getDeniedList(String channelID, byte channelType) {
        return List.of();
    }

    @Override
    public void deleteChannel(String channelID, byte channelType) {

    }

    @Override
    public void addDeniedList(String channelID, byte channelType, List<String> deniedList) {

    }

    @Override
    public void removeDeniedList(String channelID, byte channelType, List<String> deniedList) {

    }

    @Override
    public void addAllowedList(String channelID, byte channelType, List<String> allowedList) {

    }

    @Override
    public void removeAllowedList(String channelID, byte channelType, List<String> allowedList) {

    }

    @Override
    public void removeAllAllowedList(String channelID, byte channelType) {

    }

    @Override
    public List<Integer> appendMessage(String channelID, byte channelType, List<Message> messages) {
        return List.of();
    }

    @Override
    public List<Integer> appendMessageOfUser(String uid, List<Message> messages) {
        return List.of();
    }

    @Override
    public Message loadMessage(String channelID, byte channelType, int seq) {
        return null;
    }

    @Override
    public List<Message> loadLastMessages(String channelID, byte channelType, int limit) {
        return List.of();
    }

    @Override
    public List<Message> loadLastMessagesWithEnd(String channelID, byte channelType, int limit, int end) {
        return List.of();
    }

    @Override
    public List<Message> loadPrevRangeMsgs(String channelID, byte channelType, int limit, int start, int end) {
        return List.of();
    }

    @Override
    public List<Message> loadNextRangeMsgs(String channelID, byte channelType, int limit, int start, int end) {
        return List.of();
    }

    @Override
    public int getLastMessageSeq(String channelID, byte channelType) {
        return 0;
    }

    @Override
    public int getMessageOfUserCursor(String uid) {
        return 0;
    }

    @Override
    public List<Message> syncMessageOfUser(String uid, int startMsgSeq, int limit) {
        return List.of();
    }

    @Override
    public void appendMessageOfNotifyQueue(List<Message> messages) {

    }

    @Override
    public List<Message> getMessagesOfNotifyQueue(int count) {
        return List.of();
    }

    @Override
    public void removeMessageOfNotifyQueue(List<Long> messageIds) {

    }

    @Override
    public void deleteChannelAndClearMessage(String channelID, byte channelType) {

    }

    @Override
    public void addOrUpdateConversations(String uid, List<Conversation> conversations) {

    }

    @Override
    public List<Conversation> getConversations(String uid) {
        return List.of();
    }

    @Override
    public Conversation getConversation(String channelID, byte channelType, String uid) {
        return null;
    }

    @Override
    public void deleteConversation(String channelID, byte channelType, String uid) {

    }

    @Override
    public void addSystemUIDs(List<String> systemUIDs) {

    }

    @Override
    public void removeSystemUIDs(List<String> systemUIDs) {

    }

    @Override
    public List<String> getSystemUIDs() {
        return List.of();
    }

    @Override
    public void saveStreamMeta(Stream.Meta meta) {

    }

    @Override
    public void streamEnd(String channelID, byte channelType, String streamNo) {

    }

    @Override
    public Stream.Meta getStreamMeta(String channelID, byte channelType, String streamNo) {
        return null;
    }

    @Override
    public int appendStreamItem(String channelID, byte channelType, String streamNo, Stream.Item item) {
        return 0;
    }

    @Override
    public List<Stream.Item> getStreamItems(String channelID, byte channelType, String streamNo) {
        return List.of();
    }

    @Override
    public void addIpBlockList(List<String> ips) {

    }

    @Override
    public void removeIpBlockList(List<String> ips) {

    }

    @Override
    public List<String> getIpBlockList() {
        return List.of();
    }
}
