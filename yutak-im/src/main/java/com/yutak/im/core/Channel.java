package com.yutak.im.core;

import java.util.concurrent.ConcurrentHashMap;

public class Channel {
    private final ConcurrentHashMap<String ,Boolean> blackList = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String ,Boolean> whiteList = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String ,Boolean> subscriberList = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String ,Boolean> tmpSubscriberList = new ConcurrentHashMap<>();

    public void loadData(){

    }
    public void initChannelInfo() {

    }

    public void initSubscribers() {

    }
    public void initBlackList() {

    }
    public void initWhiteList() {

    }

}
