package com.yutak.im.core;

import com.yutak.im.store.H2Store;
import com.yutak.im.store.Store;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SystemUIDManager {
    private final ConcurrentHashMap<String, Boolean> uidMap;
    private Store store;
    private final static SystemUIDManager instance ;
    static {
        instance = new SystemUIDManager();
    }
    private SystemUIDManager() {
        this.uidMap = new ConcurrentHashMap<>();
        store  = H2Store.get();
    }
    public static SystemUIDManager get() {
        return instance;
    }
    public void addSystemUIDs(List<String> uids) {
        if (uids == null || uids.isEmpty()) {
            return;
        }
        // persistence
        store.addSystemUIDs(uids);
        for (String uid : uids) {
            this.uidMap.put(uid, true);
        }
    }
    public void loadSystemUIDs() {
        List<String> uid = store.getSystemUIDs();
        if (uid == null || uid.isEmpty()) {
            return;
        }
        uid.forEach(t-> this.uidMap.put(t, true));
    }
    public void removeSystemUIDs(List<String> uids) {
        if (uids == null || uids.isEmpty()) {
            return;
        }
        store.removeSystemUIDs(uids);
        uids.forEach(this.uidMap::remove);
    }
    public boolean isSystemUID(String uid) {
        return uidMap.get(uid) != null;
    }
}
