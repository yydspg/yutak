package com.yutak.im.domain;

import com.yutak.im.proto.Packet;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class PersonChannel extends Channel {
    public String id;
    public List<String> subscribers;
    public boolean ban;
    public List<Packet> packets;
    private final ReentrantLock lock ;
    public PersonChannel() {
        lock = new ReentrantLock();
    }
    public void addSubscriber(String subscriber) {
        lock.lock();
        subscribers.add(subscriber);
        lock.unlock();
    }
    public void removeSubscriber(String subscriber) {
        lock.lock();
        subscribers.remove(subscriber);
        lock.unlock();
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
