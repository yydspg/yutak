package com.yutak.im.store;

import com.yutak.im.domain.Conversation;
import com.yutak.im.domain.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;
@Slf4j
public class YutakMsgStore {
    private final ConcurrentHashMap<Integer,Slot> slots;
    public final ThreadPoolExecutor executor; //executor pool
    public static final YutakMsgStore instance = new YutakMsgStore();
    private YutakMsgStore() {
        slots = new ConcurrentHashMap<>();
        executor = new ThreadPoolExecutor(5, 10, 200, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }
    public static YutakMsgStore get(){
        return instance;
    }
    public List<Integer> appendMessages(String channelID, int channelType, List<Message> messages){
        return getTopic(channelID, channelType).appendMessages(messages);
    }
    public CompletableFuture<List<Integer>> appendMessagesAsync(String channelID, int channelType, List<Message> messages){
        return CompletableFuture.supplyAsync(()->{
            return appendMessages(channelID, channelType, messages);
        },executor);
    }
    public Message loadMessage(String channelID,int channelType,int seq) {
        return getTopic(channelID,channelType).readMessageAt(seq);
    }
    public CompletableFuture<Message> loadMessageAsync(String channelID, int channelType,int seq) {
        return CompletableFuture.supplyAsync(()->loadMessage(channelID,channelType,seq),executor);
    }
    public List<Message> loadLastMessages(String channelID,int channelType,int limit) {
        return getTopic(channelID,channelType).readLastMessages(limit);
    }
    public CompletableFuture<List<Message>> loadLastMessagesAsync(String channelID, int channelType,int limit) {
        return CompletableFuture.supplyAsync(()->loadLastMessages(channelID,channelType,limit),executor);
    }
    public List<Message> loadLastMessagesWithEnd(String channelID,int channelType, int limit,int endMessageSeq) {
        return getTopic(channelID,channelType).readLastMessagesWithEnd(limit,endMessageSeq);
    }
    public CompletableFuture<List<Message>> loadLastMessagesWithEndAsync(String channelID, int channelType,int limit,int end) {
        return CompletableFuture.supplyAsync(()->loadLastMessagesWithEnd(channelID,channelType,limit,end),executor);
    }
    // TODO  :  not impl
    public List<Message> loadPrevMessages(String channelID,int channelType,int limit,int start,int end) {
        return null;
    }
    public CompletableFuture<List<Message>> loadPrevMessagesAsync(String channelID, int channelType,int limit,int start,int end) {
        return CompletableFuture.supplyAsync(()->{
            return null;
        },executor);
    }
    public List<Message> loadNextMessages(String channelID,int channelType,int limit,int start,int end) {
        return null;
    }
    public CompletableFuture<List<Message>> loadNextMessagesAsync(String channelID, int channelType,int limit,int start,int end) {
        return CompletableFuture.supplyAsync(() -> {
            return null;
        },executor);
    }
    // this method do not need async to run
    public int getLastMessageSeq(String channelID,int channelType) {
        return getTopic(channelID,channelType).getLastMessageSeq();
    }
    public int getMessageOfUserCursor(String uid) {
        return 0;
    }
    public CompletableFuture<Integer> getMessageOfUserCursorAsync(String uid) {
        return CompletableFuture.supplyAsync(()->{
            return 0;
        },executor);
    }
    public List<Message> syncMessagesOfUser(String uid,int startMessageSeq,int limit) {
        return null;
    }
    public CompletableFuture<List<Message>> syncMessagesOfUserAsync(String uid, int startMessageSeq, int limit) {
        return CompletableFuture.supplyAsync(()->{
            return null;
        },executor);
    }

    public void clearChannelMessages(String channelID,int channelType) {

    }
    public CompletableFuture<Void> clearChannelMessagesAsync(String channelID, int channelType) {
        return CompletableFuture.runAsync(()->{

        },executor);
    }

    //stream
    public void saveStreamMeta(Model.StreamMeta meta) {
        getTopic(meta.channelID,meta.channelType).saveStreamMeta(meta);
    }
    public CompletableFuture<Void> saveStreamMetaAsync(Model.StreamMeta meta) {
        return CompletableFuture.runAsync(()->{
            saveStreamMeta(meta);
        },executor);
    }
    public void streamEnd(String channelID,int channelType,String streamNo){
        getTopic(channelID,channelType).streamEnd(streamNo);
    }
    public CompletableFuture<Void> streamEndAsync(String channelID, int channelType, String streamNo) {
        return CompletableFuture.runAsync(()->{
            streamEnd(channelID,channelType,streamNo);
        },executor);
    }
    public Model.StreamMeta getStreamMeta(String channelID, int channelType,String streamNo) {
        return getTopic(channelID,channelType).readStreamMeta(streamNo);
    }
    public CompletableFuture<Model.StreamMeta> getStreamMetaAsync(String channelID, int channelType, String streamNo) {
        return CompletableFuture.supplyAsync(()-> getStreamMeta(channelID,channelType,streamNo),executor);
    }
    public int appendStreamItem(String channelID,int channelType,String streamNo,Model.StreamItem item) {
        return getTopic(channelID,channelType).appendStreamItem(streamNo,item);
    }
    public CompletableFuture<Integer> appendStreamItemAsync(String channelID, int channelType, String streamNo, Model.StreamItem item) {
        return CompletableFuture.supplyAsync(()->appendStreamItem(channelID,channelType,streamNo,item),executor);
    }
    public List<Model.StreamItem> getStreamItems(String channelID, int channelType,String streamNo) {
        return getTopic(channelID,channelType).getStreamItems(streamNo);
    }
    public CompletableFuture<List<Model.StreamItem>> getStreamItemsAsync(String channelID, int channelType, String streamNo) {
        return CompletableFuture.supplyAsync(()->getStreamItems(channelID,channelType,streamNo),executor);
    }
    // key method
    private Topic getTopic(String channelID, int channelType) {
        log.info("current topic is {}", channelID);
        String topic = Kit.buildTopicKey(channelID, channelType);
        int slotNum = Kit.slotNum(topic);
        Slot s;
        s = slots.get(slotNum);
        if(s == null) {
            s = new Slot(slotNum);
            slots.put(slotNum, s);
        }
        return s.getTopic(topic);
    }
}
