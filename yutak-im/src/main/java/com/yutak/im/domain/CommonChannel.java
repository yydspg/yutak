package com.yutak.im.domain;

import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class CommonChannel extends Channel {
    public String id;
    public byte type;
    public boolean ban;
    public boolean large;
    public boolean disband;
    private Map<String,Boolean> blockList;
    private Map<String,Boolean> whiteList;
    private Map<String,Boolean> subscribers;
    private Map<String,Boolean> tmpSubscribers;

    public CommonChannel() {
        blockList = new ConcurrentHashMap<>();
        whiteList = new ConcurrentHashMap<>();
        subscribers = new ConcurrentHashMap<>();
        tmpSubscribers = new ConcurrentHashMap<>();
    }
    // return true means add correct
    // return false means subscriber already success or s is null
    public boolean addSubscriber(String s) {
       if(s == null) return false;
       if(subscribers.containsKey(s)) return false;
       subscribers.put(s, true);
       return true;
    }
    public void addBlockList(List<String> list) {
        for (String s : list) {
            blockList.put(s, true);
        }
    }
    public void addWhiteList(List<String> list) {
        list.forEach(s -> whiteList.put(s, true));
    }

    @Override
    public boolean baned() {
        return ban;
    }

    @Override
    public List<String> getSubscribedUsers() {
        return subscribers.keySet().stream().toList();
    }
    public void addTmpSubscriber(List<String> tmp) {
        if (tmp != null && !tmp.isEmpty()) {
            ConcurrentHashMap<String, Boolean> tmpMap = new ConcurrentHashMap<>();
            tmp.forEach(t->{tmpMap.put(t,true);});
            tmpSubscribers.putAll(tmpMap);
        }
    }
    public void removeAllTmpSubscriber() {
        tmpSubscribers.clear();
    }
    public void removeAllSubscriber() {
        subscribers.clear();
    }
    public void removeSubscribers(List<String> subscribers) {
        subscribers.forEach(t->{tmpSubscribers.remove(t);});
    }
    public void removeBlockList(List<String> list) {
        list.forEach(t->{blockList.remove(t);});
    }
    public void removeWhiteList(List<String> list) {
        list.forEach(t->{whiteList.remove(t);});
    }
    public List<String> getTmpSubscribers() {
        return tmpSubscribers.keySet().stream().toList();
    }
    public List<String> getWhiteList() {
        return whiteList.keySet().stream().toList();
    }
    public static void main(String[] args) {
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
        map.put("id","1");
        System.out.println(map.get("id"));
        System.out.println(map.containsKey("id"));
    }
}
