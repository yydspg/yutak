package com.yutak.im.core;

import com.yutak.im.domain.Conn;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.net.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// 业务连接管理
public class ConnectManager {

    private final AtomicInteger onlineCount ;
    private final ConcurrentHashMap<String, List<Long>> users;
    private final ConcurrentHashMap<Long,Conn> connects;

    private final static ConnectManager connectManager ;
    static {
        connectManager = new ConnectManager();
    }
    private ConnectManager() {
        this.onlineCount = new AtomicInteger(0);
        this.users = new ConcurrentHashMap<>();
        this.connects = new ConcurrentHashMap<>();
    }
    public static ConnectManager get() {
        return connectManager;
    }
    public void addConnect(Conn conn) {
        List<Long> uids = users.get(conn.uid);
        if (uids == null) uids = new ArrayList<>(2);
        uids.add(conn.id);
        users.put(conn.uid, uids);
        connects.put(conn.id,conn);
        onlineCount.incrementAndGet();
    }
    public Conn getConnect(long id) {
        return connects.get(id);
    }
    public void removeConnect(long id) {
        Conn conn = connects.get(id);
        List<Long> ids = users.get(conn.uid);
        // remove real connect
        connects.remove(id);
        // remove connect in user model
        onlineCount.decrementAndGet();
        for (int i = 0; i < ids.size(); i++) {
            if (ids.get(i) == id) {
                ids.remove(i);
                break;
            }
        }
    }
    public List<Conn> getConnect(String uid) {
        List<Long> ids = users.get(uid);
        if (ids == null) {
            return null;
        }
        List<Conn> conns = new ArrayList<>(ids.size());
        ids.forEach(id -> conns.add(connects.get(id)));
        return conns;
    }
    public int getOnlineCount() {
        return onlineCount.get();
    }
    public boolean existConnect(String uid) {
        return users.get(uid).size() > 0;
    }
    public Conn getConnectWithDeviceID(String uid, String deviceID) {
        List<Long> ids = users.get(uid);
        for (long id : ids) {
            Conn conn = connects.get(id);
            if (conn.deviceID.equals(deviceID)) {
                return conn;
            }
        }
        return null;
    }
    public List<Conn> getConnectWithDeviceFlag(String uid,byte deviceFlag) {
        List<Long> ids = users.get(uid);
        List<Conn> conns = new ArrayList<>();
        if (ids == null || ids.size() == 0) return null;
        for (long id : ids) {
            Conn conn = connects.get(id);
            if (conn.deviceFlag == deviceFlag) {conns.add(conn);}
        }
        return conns;
    }
    public int getConnectNumWithDeviceFlag(String uid,String deviceFlag) {
        List<Long> ids = users.get(uid);
        int res = 0;
        for (long id : ids) {
            Conn conn = connects.get(id);
            if (conn.deviceID.equals(deviceFlag)) {
                res += 1;
            }
        }
        return res;
    }
    public List<Conn> getOnlineConn(List<String> uids) {
        if (uids == null) return Collections.emptyList();
        ArrayList<Conn> conns = new ArrayList<>();
        uids.forEach(uid -> {
            List<Conn> conn = getConnect(uid);
            if (conn != null && !conn.isEmpty()) {
                conn.stream().filter(t-> !t.close.get()).forEach(conns::add);
            }
        });
        return conns;
    }
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        NetServerOptions netServerOptions = new NetServerOptions();
        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setWorkerPoolSize(10);
        netServerOptions.setHost("127.0.0.1");
        netServerOptions.setPort(8080);
        NetServer netServer = vertx.createNetServer();

        netServer.connectHandler(s->{
//            s.handler(buffer -> {
//                String string = buffer.getString(0, buffer.length());
//                System.out.println("received:" + string);
//                Packet packet = BufferKit.decodePacket(buffer);
//            });
            System.out.println("事件分配线程"+Thread.currentThread().getName());
            s.write("hello 1");
            s.write("hello 2");
            vertx.executeBlocking(()->{
                System.out.println("开始阻塞这个工作线程"+Thread.currentThread().getName());
                s.write("hello 3");
                Future.future(p->{
                    System.out.println("假装异步"+Thread.currentThread().getName());
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    p.complete("hello 4");
                }).onComplete(t->{
                    if (t.succeeded()) {
                        System.out.println("回调的线程地址"+Thread.currentThread().getName());
                    }
                });
                return null;
            });
//            s.end();
        });
        netServer.listen(8080);
        NetClient netClient = vertx.createNetClient(new NetClientOptions());
        ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(1);
        pool.scheduleAtFixedRate(()->{
            Future<NetSocket> connect = netClient.connect(8080, "127.0.0.1");
            connect.onComplete(ar -> {
                NetSocket socket = ar.result();
//                ConnectPacket c = new ConnectPacket();
//                c.UID = "cvasdffwefwe";
//                c.clientTimestamp = System.currentTimeMillis();
//                c.deviceID = "gradewefwef";
//                c.token = "segvefewfw";
//                c.deviceFlag = 1;
//                c.clientKey = "cefcefwefAWEF";
//                Buffer f = c.encode();
//                socket.write(f);
                socket.handler(t->{
                    t.getString(0, t.length());
                });
            });
        },1,1, TimeUnit.SECONDS);
    }

}
