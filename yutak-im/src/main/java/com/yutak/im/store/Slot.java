package com.yutak.im.store;

import com.yutak.im.core.LRU;

public class Slot {

    public LRU<String,Topic> topicLRU;
    public int num;
    public Slot(int num) {
        this.topicLRU = new LRU<>();
        this.num = num;
    }
    public Topic getTopic(String topic) {
        Topic t = topicLRU.get(topic);
        if (t == null) {
            t = new Topic(topic,num);
            topicLRU.put(topic,t);
        }
        return t;
    }
    public void close(){
        topicLRU.clear();
    }
}
