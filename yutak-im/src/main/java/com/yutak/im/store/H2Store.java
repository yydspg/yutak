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
import java.util.ArrayList;
import java.util.List;

public class H2Store implements Store {
    public HikariDataSource hikariDataSource;
    public Logger log;
    private List<String> sql;
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
        sql = new ArrayList<>();
        // 0
        sql.add("select token from user_token where uid = ?");
        // 1
        sql.add("update user_token set token = ?,device_level = ? where uid = ?");
        // 2
        sql.add("select device_level from user_token where uid = ?");
        // 3
        sql.add("select 1 from channel_info where channel_id = ? limit 1");
        // 4
        sql.add("select * from  channel_info where channel_id = ?");
        // 5
        sql.add("update channel_info set ban = ?,disband = ?,large = ? where channel_id = ?");
        // 6
        sql.add("insert into channel_info (channel_id,ban,disband,large) values (?,?,?,?)");
        // 7
        sql.add("insert into ip_block (ip) values (?)");
        // 8
        sql.add("select ip from ip_block");
        // 9
        sql.add("delete from ip_block where ip = ?");
    }

    @SneakyThrows
    public PreparedStatement get(String sql) {
        return hikariDataSource.getConnection().prepareStatement(sql);
    }
    public static void main(String[] args) throws SQLException {
        H2Store h2Store = new H2Store();
        System.out.println(h2Store.getUserToken("123", (byte) 0));
        h2Store.updateUserToken("123","frhjgf0",(byte) 0,(byte)4);
        System.out.println(h2Store.getUserDeviceLevel("123", (byte) 0));
        System.out.println(h2Store.existChannel("123",(byte) 0));
        System.out.println(h2Store.getChannel("123",(byte) 0));
        ChannelInfo c = new ChannelInfo();
        c.channelId = "1234";
        c.channelType =(byte) 0;
        c.large = true;
        c.disband = false;
        c.ban = true;
        h2Store.addOrUpdateChannel(c);
        ArrayList<String> ips = new ArrayList<>();
        ips.add("127.0.0.1");
        ips.add("127.0.0.2");
        h2Store.addIpBlockList(ips);
        List<String> blockList = h2Store.getIpBlockList();
        blockList.forEach(System.out::println);
        h2Store.removeIpBlockList(ips);
    }

    @Override
    public void open() {

    }

    @Override
    public void close() {

    }


    @SneakyThrows
    @Override
    public String getUserToken(String uid, byte deviceFlag) {
        // 由于此数据库只对内提供供服务，不存在sql注入问题
        PreparedStatement p = get(sql.get(0));
        p.setString(1,uid+deviceFlag);
        ResultSet set = p.executeQuery();
//        System.out.println("SELECT TOKEN FROM USER_TOKEN WHERE UID = '" + uid+deviceFlag + "'");
//        ResultSet set = query("SELECT * FROM USER_TOKEN");
        set.next();
        return set.getString(1);
    }

    @SneakyThrows
    @Override
    public byte getUserDeviceLevel(String uid, byte deviceFlag) {
        PreparedStatement p = get(sql.get(2));
        p.setString(1,uid+deviceFlag);
        ResultSet set = p.executeQuery();
        set.next();
        byte b = set.getByte(1);
        set.close();
        p.close();
        return b;
    }

    @SneakyThrows
    @Override
    public void updateUserToken(String uid, String token, byte deviceFlag, byte deviceLevel) {
        PreparedStatement p = get(sql.get(1));
        p.setString(1,token);
        p.setByte(2,deviceLevel);
        p.setString(3,uid+deviceFlag);
        p.executeUpdate();
        p.close();
    }

    @Override
    public void updateMessageOfUserCursorNeed(String uid, int messageSeq) {

    }

    @SneakyThrows
    @Override
    public ChannelInfo getChannel(String channelID, byte channelType) {
        PreparedStatement p = get(sql.get(4));
        p.setString(1,channelID+channelType);
        ResultSet set = p.executeQuery();
        set.next();
        ChannelInfo c = new ChannelInfo();
        c.channelId = channelID;
        c.channelType  = channelType;
        c.ban = set.getBoolean(3);
        c.disband = set.getBoolean(4);
        c.large = set.getBoolean(5);
        set.close();
        p.close();
        return c;
    }

    @SneakyThrows
    @Override
    public void addOrUpdateChannel(ChannelInfo channelInfo) {
        if(existChannel(channelInfo.channelId,channelInfo.channelType)){
            // update
            PreparedStatement p = get(sql.get(5));
            p.setBoolean(1,channelInfo.ban);
            p.setBoolean(2,channelInfo.disband);
            p.setBoolean(3,channelInfo.large);
            p.setString(4,channelInfo.channelId+channelInfo.channelType);
            p.executeUpdate();
            p.close();
        }else {
            // insert
            PreparedStatement p = get(sql.get(6));
            p.setString(1, channelInfo.channelId + channelInfo.channelType);
            p.setBoolean(2, channelInfo.ban);
            p.setBoolean(3, channelInfo.disband);
            p.setBoolean(4, channelInfo.large);
            p.execute();
            p.close();
        }
    }

    @SneakyThrows
    @Override
    public boolean existChannel(String channelID, byte channelType) {
        PreparedStatement p = get(sql.get(3));
        p.setString(1,channelID+channelType);
        ResultSet set = p.executeQuery();
        set.next();
        try {
            if (set.getInt(1) == 1) {
                set.close();
                p.close();
                return true;
            }
        }catch (Exception e) {
            set.close();
            p.close();
            return false;
        }
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

    @SneakyThrows
    @Override
    public void addIpBlockList(List<String> ips) {
        // 目前不可以违反主键约束
        PreparedStatement p = get(sql.get(7));
        for (int i = 0; i < ips.size(); i++) {
            p.setString(1, ips.get(i));
            p.addBatch();
        }
        p.executeBatch();
        p.close();
    }

    @SneakyThrows
    @Override
    public void removeIpBlockList(List<String> ips) {
        PreparedStatement p = get(sql.get(9));
        for (int i = 0; i < ips.size(); i++) {
            p.setString(1,ips.get(i));
            p.addBatch();
        }
        p.executeBatch();
        p.close();
    }

    @SneakyThrows
    @Override
    public List<String> getIpBlockList() {
        PreparedStatement p = get(sql.get(8));
        ResultSet set = p.executeQuery();
        ArrayList<String> ips = new ArrayList<>();
        while(set.next()) {
            ips.add(set.getString(1));
        }
        set.close();
        p.close();
        return ips;
    }
}
