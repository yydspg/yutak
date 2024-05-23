package com.yutak.im.core;

import com.yutak.im.domain.Conn;
import com.yutak.im.kit.BufferKit;
import com.yutak.im.proto.ConnectPacket;
import com.yutak.im.proto.Packet;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

// 业务连接管理
public class ConnectManager {

    private ReentrantLock lock = new ReentrantLock();
    public  ConcurrentHashMap<String, List<Long>> users;
    public ConcurrentHashMap<Long,Conn> connects;

    public ConnectManager() {
        this.users = new ConcurrentHashMap<>();
        this.connects = new ConcurrentHashMap<>();
    }
    public void addConnect(Conn conn) {
        List<Long> uids = users.get(conn.uid);
        if (uids == null) uids = new ArrayList<>(2);
        lock.lock();
        uids.add(conn.id);
        users.put(conn.uid, uids);
        connects.put(conn.id,conn);
        lock.unlock();
    }
    public Conn getConnect(long id) {
        return connects.get(id);
    }
    public void removeConnect(long id) {
        Conn conn = connects.get(id);
        List<Long> ids = users.get(conn.uid);
        lock.lock();
        connects.remove(id);
        for (int i = 0; i < ids.size(); i++) {
            if (ids.get(i) == id) {
                ids.remove(i);
                break;
            }
        }
        lock.unlock();
    }
    public List<Conn> getConnect(String uid) {
        List<Long> ids = users.get(uid);
        List<Conn> conns = new ArrayList<>(ids.size());
        ids.forEach(id -> conns.add(connects.get(id)));
        return conns;
    }
    public boolean existConnect(String uid) {
        return users.get(uid).size() > 0;
    }
    public Conn getConnectWithDeviceID(String uid, String deviceID) {
        List<Long> ids = users.get(uid);
        for (Long id : ids) {
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
        for (Long id : ids) {
            Conn conn = connects.get(id);
            if (conn.deviceFlag == deviceFlag) {conns.add(conn);}
        }
        return conns;
    }
    public int getConnectNumWithDeviceFlag(String uid,String deviceFlag) {
        List<Long> ids = users.get(uid);
        int res = 0;
        for (Long id : ids) {
            Conn conn = connects.get(id);
            if (conn.deviceID.equals(deviceFlag)) {
                res += 1;
            }
        }
        return res;
    }
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        NetServerOptions netServerOptions = new NetServerOptions();
        netServerOptions.setHost("127.0.0.1");
        netServerOptions.setPort(8080);
        NetServer netServer = vertx.createNetServer();

        netServer.connectHandler(s->{
//            s.handler(buffer -> {
//                String string = buffer.getString(0, buffer.length());
//                System.out.println("received:" + string);
//                Packet packet = BufferKit.decodePacket(buffer);
//            });
            s.write("hello 1");
            s.write("hello 2");
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
                    System.out.println("client received:" + t.getString(0, t.length()));
                });
            });
        },1,1, TimeUnit.SECONDS);
    }

}
