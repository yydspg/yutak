package com.yutak.im.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Cache<K,V> {

    private final ConcurrentHashMap<K,V> caches;
    private final ConcurrentHashMap<K,AtomicInteger> flags;
    private static final int DEFAULT_MAX_CAPACITY = 1000;
    private final ScheduledThreadPoolExecutor executor;
    private final AtomicInteger size;
    private int CUSTOME_CAPACITY;
    private Set<K> prefix;
    private String name;
    private final Logger log = LoggerFactory.getLogger(Cache.class);
    public Cache(String name) {
        this(name,DEFAULT_MAX_CAPACITY);
    }

    public Cache(String name, int maxCapacity) {
        caches = new ConcurrentHashMap<>(maxCapacity);
        flags = new ConcurrentHashMap<>(maxCapacity);
        executor = new ScheduledThreadPoolExecutor(2);
        size = new AtomicInteger(0);
        prefix = new HashSet<>(maxCapacity);
        CUSTOME_CAPACITY = maxCapacity;
        this.name=name;
        decrement();
        release();
    }
    public void put(K k, V v) {
        caches.put(k, v);
        flags.put(k, new AtomicInteger(5));
        size.getAndIncrement();
        log.debug("{}put ops,current size:{}",name,size.get());
    }
    public V get(K k) {
        // check if this exists
        if (flags.get(k) == null) {
            return null;
        } else if (flags.get(k).get() == 5){
            // check whether reach the max size
        } else {
            // do not reach thi limit ,just increment this atomicInteger
            flags.get(k).getAndIncrement();
        }
        return caches.get(k);
    }
    public int getSize() {
        return flags.size();
    }
    private void decrement() {
        executor.scheduleAtFixedRate(()->{
            // single thread execute
            prefix = flags.keySet();
            for(K k : prefix) {
                if (flags.get(k).decrementAndGet() == -1) {
                    flags.remove(k);
                    caches.remove(k);
                    size.getAndDecrement();
                        log.info("remove k;{}",k);
                }
            }
            log.debug("{}decrement,current size:{}",name,size.get());
        },1,1, TimeUnit.MINUTES);

    }
    public void destroy() {
        caches.clear();
        flags.clear();
        size.set(0);
    }
    private void release() {
        executor.scheduleAtFixedRate(()->{
            // custom over float
            if(CUSTOME_CAPACITY > 0 && CUSTOME_CAPACITY < size.get()) {
                simpleRelease();
            }
            if (DEFAULT_MAX_CAPACITY < size.get()) {
                simpleRelease();
            }
        },1,5, TimeUnit.MINUTES);
    }
//    private void randomRelease() {
//        log.warn("Cache:{}, exceeding limit!,current size:{}",name,size.get());
//        Random r = new Random();
//        List<K> list = prefix.stream().toList();
//        for (int i = 0; i < 200; i++) {
//            int random = r.nextInt(size.get());
//            K k = list.get(random);
//            caches.remove(k);
//            flags.remove(k);;
//            size.getAndDecrement();
//        }
//    }
    private void simpleRelease() {
        log.warn("Cache:{}, exceeding limit!,current size:{}",name,size.get());
        caches.clear();
        flags.clear();
        size.getAndSet(0);
    }
    public static String generateRandomString(int length) {
        // 定义字符集
        String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        // 创建Random对象
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            // 生成随机索引
            int index = random.nextInt(charSet.length());
            // 根据索引从字符集中选取字符并添加到StringBuilder中
            sb.append(charSet.charAt(index));
        }
        return sb.toString();

    }
}
