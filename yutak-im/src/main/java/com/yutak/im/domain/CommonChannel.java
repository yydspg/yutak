package com.yutak.im.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class CommonChannel implements Channel {
    public String id;
    public byte type;
    public boolean ban;
    public boolean large;
    public boolean disband;
    private List<String> blockList;
    private List<String> whiteList;
    private List<String> subscribers;
    private List<String> tmpSubscribers;
    private ReentrantLock lock;

    public CommonChannel() {
        blockList = new ArrayList<>();
        whiteList = new ArrayList<>();
        subscribers = new ArrayList<>();
        tmpSubscribers = new ArrayList<>();
        lock = new ReentrantLock();
    }

    public void addSubscriber(String subscriber) {
        lock.lock();
        subscribers.add(subscriber);
        lock.unlock();
    }
    public void addBlockList(String block) {
        lock.lock();
        blockList.add(block);
        lock.unlock();
    }
    public void addWhiteList(String white) {
        lock.lock();
        whiteList.add(white);
        lock.unlock();
    }
}
