package com.yutak.client;

import com.yutak.vertx.kit.UUIDKit;
import io.vertx.core.Vertx;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ClientManager {
    public static Vertx vertx;
    public static AtomicLong count  = new AtomicLong(0);
    static {
        vertx = Vertx.vertx();
    }
    private static final ConcurrentHashMap<String,Client> clients = new ConcurrentHashMap<>();

    public static void put(Client client) {
        clients.put(client.UID,client);
    }
    public static String getUID() {
        return   String.valueOf(count.incrementAndGet());
    }
    public static void buildClient(int num) {
        for (int i = 0; i < num; i++) {
            Client c = new Client();
            c.build();
        }
    }
    public static void main(String[] args) throws InterruptedException {
//        // build enough tcp client
//        ClientManager manager = new ClientManager();
//        manager.buildClient(3);
    }
}
