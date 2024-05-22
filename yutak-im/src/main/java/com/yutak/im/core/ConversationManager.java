package com.yutak.im.core;

import com.yutak.im.domain.Conversation;
import com.yutak.im.domain.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

// 会话管理
public class ConversationManager {
    private BlockingQueue<Bucket> queue;
    private ConcurrentHashMap<String, LinkedHashMap<String, Conversation>> userConversations;
    private ConcurrentHashMap<String,Boolean> conversationPersistence;
    private int bucketNum;
    private static final Logger log = LoggerFactory.getLogger(ConversationManager.class);
    private Options options;
    public ConversationManager() {

    }

    public static class Bucket {
        public Message message;
        public List<String> subscribers;
    }
    public void pushMessage(Message message,List<String> subscribers) {
        if (!options.conversationConfig.recentOn) return;
        Bucket bucket = new Bucket();
        bucket.message = message;
        bucket.subscribers = subscribers;
        queue.put(bucket);
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
