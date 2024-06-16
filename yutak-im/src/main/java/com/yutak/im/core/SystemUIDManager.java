package com.yutak.im.core;

import com.yutak.im.store.YutakStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SystemUIDManager {
    private static final Logger log = LoggerFactory.getLogger(SystemUIDManager.class);
    private final Map<String, Boolean> UIDMap;
    private final YutakStore yutakStore;
    private final static SystemUIDManager instance ;
    static {
        instance = new SystemUIDManager();
    }
    private SystemUIDManager() {
        this.UIDMap = new ConcurrentHashMap<>();
        yutakStore = YutakStore.get();
    }
    public static SystemUIDManager get() {
        return instance;
    }
    public void addSystemUIDs(List<String> uids) {
        if (uids == null || uids.isEmpty()) {
            return;
        }
        Set<String> set = UIDMap.keySet();
        List<String> newUids = new ArrayList<>(uids);
        for (String uid : uids) {
            if (!set.contains(uid)) {
                UIDMap.put(uid, true);
                newUids.add(uid);
            }
        }
        // persistence
        yutakStore.addSystemUIDs(newUids);
    }
    public void destroy() {
        UIDMap.clear();
        log.info("yutak ==> SystemUIDManager destroy");
    }
    public void loadSystemUIDs() {
        yutakStore.getSystemUIDs().whenComplete((r,e)->{
            if(e != null) {
                log.error("System UID load error", e);
            }
            if(r != null) {
                r.forEach(t->{
                    UIDMap.put(t,true);
                });
            }
        });
    }
    public void removeSystemUIDs(List<String> uids) {
        if (uids == null || uids.isEmpty()) {
            return;
        }
        Set<String> set = UIDMap.keySet();
        List<String> newUids = new ArrayList<>(uids);
        for (String uid : uids) {
            if (set.contains(uid)) {
                UIDMap.remove(uid);
                newUids.remove(uid);
            }
        }
        yutakStore.removeSystemUIDs(newUids);
    }
    public boolean isSystemUID(String uid) {
        return UIDMap.get(uid) != null;
    }
}
