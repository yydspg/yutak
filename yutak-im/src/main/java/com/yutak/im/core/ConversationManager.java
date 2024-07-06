package com.yutak.im.core;

import com.yutak.im.domain.Channel;
import com.yutak.im.domain.Conversation;
import com.yutak.im.domain.Message;
import com.yutak.im.store.YutakStore;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

// 会话管理
public class ConversationManager {

    private BlockingQueue<Bucket> queue;
    private LRU<String, Map<String,Conversation>> userConversations;
    private ConcurrentHashMap<String,Boolean> conversationPersistence;
    private static final Logger log = LoggerFactory.getLogger(ConversationManager.class);
    private Options options;
    private YutakStore yutakStore;

    public ConversationManager() {
        yutakStore = YutakStore.get();
    }

    public static class Bucket {
        public Message message;
        public List<String> subscribers;
    }
    @SneakyThrows
    public void pushMessage(Message message, List<String> subscribers) {
        if (!options.conversationConfig.recentOn) return;
        Bucket bucket = new Bucket();
        bucket.message = message;
        bucket.subscribers = subscribers;
        queue.put(bucket);
    }
    public List<Conversation> getConversations(String uid, long version, List<Channel> channels) {
        List<Conversation> conversations = new ArrayList<>();
        return null;

    }
    public void addOrUpdateConversation(String uid,Conversation conversation) {
        userConversations.get(uid).put(conversation.channelID+"-"+conversation.channelType,conversation);
        // TODO  :  persistence
    }
    public Conversation getConversation(String uid, String channelID,int channelType) {
        if (userConversations.get(uid).get(channelID) != null) {
            return userConversations.get(uid).get(channelID);
        }
        Conversation conversation = yutakStore.getConversation(uid, channelID, channelType);
        userConversations.get(uid).put(channelID+"-"+channelType, conversation);
        return conversation;
    }
    public CompletableFuture<Conversation> getConversationAsync(String uid, String channelID, int channelType) {
        return CompletableFuture.supplyAsync(()-> getConversation(uid, channelID, channelType));
    }
    private List<Conversation> getUserAllConversationsMapFromStore(String uid) {
        return null;
    }
    public CompletableFuture<Void> deleteConversation(String uid, String channelID, int channelType) {
        return CompletableFuture.runAsync(()->{
            userConversations.get(uid).remove(channelID);
            yutakStore.deleteConversation(uid, channelID, channelType);
        });
    }
    public void setConversationUnread(String uid, String channelID, int channelType,int unread,int messageSeq) {

    }
    public static void main(String[] args) {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(3);
        // 生产者线程
        new Thread(() -> {
            try {
                while (true) {
                    //TODO 关键这个 api是阻塞的，如何适配vertx呢？
                    queue.put("Task");
                    System.out.println("Produced Task");
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        // 消费者线程
        new Thread(() -> {
            try {
                while (true) {
                    String task = queue.take();
                    System.out.println("Consumed Task: " + task);
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
