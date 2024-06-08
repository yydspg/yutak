package com.yutak.im.core;

import com.yutak.im.domain.Channel;
import com.yutak.im.domain.Conn;
import com.yutak.im.domain.Message;
import com.yutak.im.proto.CS;
import com.yutak.im.proto.Packet;
import com.yutak.im.proto.RecvPacket;
import com.yutak.im.proto.SendPacket;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class DeliveryManager {

    private ChannelManager channelManager;
    private ConnectManager connectManager;
    private Options options;
    private Vertx vertx;
    private final  static DeliveryManager instance = new DeliveryManager();
    private final AtomicLong ID = new AtomicLong(0);
    private DeliveryManager() {
        vertx = YutakNetServer.get().vertx;
    }
    public static DeliveryManager get() {
        return instance;
    }

    public Handler<Channel> delivery(final Conn conn, List<SendPacket> sendPackets) {
        return  channel -> {
                List<Message> messages = new ArrayList<>(sendPackets.size());
                sendPackets.forEach(sendPacket -> {
                    long messageID = ID.incrementAndGet();
                    messages.add(buildMessage(messageID,sendPacket,conn));
                });
            List<String> subscribedUsers = channel.getSubscribedUsers();
            msg(messages,subscribedUsers,null, conn.uid, conn.deviceID, conn.deviceFlag);
        };
    }
    private void msg(List<Message> message, List<String> subscribers, Map<String,Integer> syncOnceMsgSeq,String fromUID,String fromDeviceUID,byte fromDeviceFlag) {
        if(message.size() == 0|| subscribers.size() == 0) return;

        vertx.executeBlocking(getConn(subscribers,fromUID,fromDeviceFlag,fromDeviceUID))
                .onComplete(r->{
                    if(r.succeeded()) {
                        List<Conn> conns = r.result();
                        conns.forEach(recvConn->{
                            List<RecvPacket> recvPackets = new ArrayList<>();
                            for(Message msg : message) {
                                recvPackets.add(msg.recvPacket);
                            }
                            dataOut(recvConn,recvPackets);
                        });
                    }
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
    public void dataOut(Conn conn, List packets) {
        // websocket connect or tcp

        //statistics layer
        conn.outMsgs.getAndIncrement();

        if(packets.size() == 0)return;
        packets.forEach(p -> {conn.netSocket.write(((Packet)p).encode());});
    }
    private Message buildMessage(long messageID,SendPacket s,Conn conn) {
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
        Message m = new Message();
        m.recvPacket = r;
        m.fromDeviceFlag = conn.deviceFlag;
        m.fromDeviceID = conn.deviceID;
        return m;
    }
}
