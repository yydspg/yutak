package com.yutak.im.core;

import com.yutak.im.domain.*;
import com.yutak.im.proto.CS;
import com.yutak.im.proto.Packet;
import com.yutak.im.proto.RecvPacket;
import com.yutak.im.proto.SendPacket;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
@Slf4j
public class DeliveryManager {

    private ChannelManager channelManager;
    private ConnectManager connectManager;
    private Options options;
    private final  static DeliveryManager instance = new DeliveryManager();
    private final AtomicLong ID = new AtomicLong(0);
    private DeliveryManager() {
        connectManager = ConnectManager.get();

    }
    public static DeliveryManager get() {
        return instance;
    }
    // route from tcp connect
    public Handler<CommonChannel> delivery(final Conn conn, List<SendPacket> sendPackets) {
        return  channel -> {
                List<Message> messages = new ArrayList<>(sendPackets.size());
                sendPackets.forEach(sendPacket -> {
                    long messageID = ID.incrementAndGet();
//                    messages.add(buildMessage(messageID,sendPacket,conn));
                });
            List<String> subscribedUsers = channel.getSubscribedUsers();
            routeMsg(messages,subscribedUsers,null, conn.uid, conn.deviceID, conn.deviceFlag);
        };
    }

    // process msg,set this method public aim to used in http
    public void routeMsg(List<Message> message, List<String> subscribers, Map<String,Integer> syncOnceMsgSeq,String fromUID,String fromDeviceUID,int fromDeviceFlag) {
        if(message.isEmpty() || subscribers.isEmpty()) return;

        List<Conn> conns = new ArrayList<>();
        for(String subscriber : subscribers) {
            if(!subscriber.equals(fromUID)) {
                List<Conn> connect = connectManager.getConnect(subscriber);
                if(connect != null && !connect.isEmpty()) {
                    conns.addAll(connectManager.getConnect(subscriber));
                }
            }
        }
        System.out.println("connects size:"+conns.size());
        conns.forEach(recvConn->{
            List<RecvPacket> recvPackets = new ArrayList<>();
            for(Message msg : message) {
                recvPackets.add(msg.recvPacket);
            }
            log.info("data out {}",recvPackets);
            dataOut(recvConn,recvPackets);
        });
    }
    public Handler<Promise<List<Conn>>> getConn(List<String> subscribers, String fromUID, byte deviceFlag, String fromDeviceID) {
           return p->{
               List<Conn> conns = new ArrayList<>();
               for(String subscriber : subscribers) {
                   if(!subscriber.equals(fromUID)) {
                       conns.addAll(connectManager.getConnect(subscriber));
                   }
               }
               p.complete(conns);
           };
    }
    public void dataOut(Conn conn, List<? extends Packet> packets) {
        // websocket connect or tcp

        //statistics layer
        conn.outMsgs.getAndIncrement();

        if(packets == null || packets.isEmpty())return;
        log.info("real data out{}",packets);
        for (Packet packet : packets) {
            try {
                if (conn instanceof TcpConn) {
                    ((TcpConn) conn).netSocket.write(packet.encode());
                    break;
                }
                if (conn instanceof WSConn) {
                    ((WSConn) conn).webSocket.write(packet.encode());
                    break;
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
    public Message buildMessage(long messageID, byte streamFlag,SendPacket s, Conn conn) {
        RecvPacket r = new RecvPacket();
        r.frameType = CS.FrameType.RECV;
        r.redDot = s.redDot;
        r.syncOnce = s.syncOnce;
        r.noPersist = s.noPersist;
        r.setting = s.setting;
        r.messageID = messageID;
        r.streamNo = s.streamNo;
        r.channelType = s.channelType;
        r.channelID = s.channelID;
        r.topic = s.topic;
        r.fromUID = conn.uid;
        r.expire = s.expire;
        //todo stream flag
        r.streamFlag = streamFlag;
        Message m = new Message();
        m.recvPacket = r;
        m.fromDeviceFlag = conn.deviceFlag;
        m.fromDeviceID = conn.deviceID;
        return m;
    }
}
