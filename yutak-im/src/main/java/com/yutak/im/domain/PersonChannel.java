package com.yutak.im.domain;

import java.util.List;
import java.util.Map;

public class PersonChannel extends Channel {
    //  example :"AAA@BBB"
    // fake channel type
    public String channelID;
    public Map<String,Boolean> subscribers;
    public boolean ban;
    public PersonChannel() {

    }
    public void addSubscriber(String subscriber) {
        subscribers.put(subscriber, true);
    }
    public void removeSubscriber(String subscriber) {
       subscribers.remove(subscriber);
    }

    @Override
    public boolean baned() {
        return ban;
    }

    @Override
    public List<String> getSubscribedUsers() {
        return subscribers.keySet().stream().toList();
    }

}
