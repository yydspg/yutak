package com.yutak.im.store;

import com.yutak.im.core.LRU;

import java.util.concurrent.locks.ReentrantLock;

public class Topic {
    public String name;
    public int slot;
    public int[] segments;
    public int lastBaseMessageSeq;
    public String topicDir;
    public ReentrantLock lock ;
    public LRU<String, Integer> streamLRU;
}
