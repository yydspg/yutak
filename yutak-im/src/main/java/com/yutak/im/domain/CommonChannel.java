package com.yutak.im.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class CommonChannel extends Channel {
    public String id;
    public byte type;
    public boolean ban;
    public boolean large;
    public boolean disband;
    private List<String> blockList;
    private List<String> whiteList;
    private List<String> subscribers;
    private List<String> tmpSubscribers;

    public CommonChannel() {
        blockList = new ArrayList<>();
        whiteList = new ArrayList<>();
        subscribers = new ArrayList<>();
        tmpSubscribers = new ArrayList<>();
    }

    public void addSubscriber(String subscriber) {
        subscribers.add(subscriber);
    }
    public void addBlockList(String block) {
        blockList.add(block);
    }
    public void addWhiteList(String white) {
        whiteList.add(white);
    }

    @Override
    public boolean baned() {
        return ban;
    }

    @Override
    public List<String> getSubscribedUsers() {
        return subscribers;
    }

}
