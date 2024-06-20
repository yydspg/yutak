package com.yutak.im.handler;

import com.yutak.im.core.*;
import com.yutak.im.domain.Channel;
import com.yutak.im.domain.CommonChannel;
import com.yutak.im.domain.Conn;
import com.yutak.im.kit.BufferKit;
import com.yutak.im.kit.SecurityKit;
import com.yutak.im.kit.SocketKit;
import com.yutak.im.proto.*;
import com.yutak.im.store.ChannelInfo;
import com.yutak.im.store.YutakStore;
import com.yutak.im.store.H2Store;
import com.yutak.im.store.Store;
import com.yutak.vertx.kit.StringKit;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PacketProcessor {
    private ConnectManager connectManager;
    private ChannelManager channelManager;
    private Options options;
    private final Logger log;
    private final YutakNetServer yutakNetServer;
    private final DeliveryManager deliveryManager;
    private Vertx vertx;
    private final YutakStore yutakStore;
    byte[] types = {CS.FrameType.PING,CS.FrameType.SEND,CS.FrameType.RECVACK, (byte) CS.FrameType.SUB};
    private PacketProcessor() {
        yutakNetServer = YutakNetServer.get();
        log = LoggerFactory.getLogger(this.getClass());
        connectManager = ConnectManager.get();
        channelManager = ChannelManager.get();
        vertx = YutakNetServer.get().vertx;
        options = Options.get();
        deliveryManager = DeliveryManager.get();
        yutakStore = YutakStore.get();
    }

    private final static PacketProcessor instance ;

    static {
        instance = new PacketProcessor();
    }
    public static PacketProcessor get() {
        return instance;
    }

    public Handler<Buffer> pipe(NetSocket s) {
        return b -> {
            // 1. statistics layer
            get().statistics().handle(b);
            // decode layer
            Packet packet = BufferKit.decodePacket(b);
            // check data packet,if return null means decode fail
            if (packet == null) {
                s.end();
                return;
            }
            //search for conn
            // process packet
            if (packet.frameType == CS.FrameType.CONNECT) {
                // connect status build
                final ConnectPacket connectPacket = (ConnectPacket) packet;
                // build connect
                Future.future(get().connect(s, connectPacket)).onComplete(res -> {
                    //connect success
                    if (res.succeeded() && res.result() != null) {
                        s.write((res.result()).encode());
                        return;
                    }
                    // connect fail
                    log.error(res.cause().getMessage());
                    //  end tcp connect
                    s.end();
                });
                // other packet
            } else {
                // check connect status,build connect id by socketKit
                Conn conn = connectManager.getConnect(SocketKit.ipToLong(s.remoteAddress().host()));

                if(conn == null) {
                    log.error("please connect first");
                    s.end();
                    return;
                }
                conn.inMsgs.getAndIncrement();
                conn.packets.add(packet);
                //  process msg
                // TODO  :  这里存在问题啊，packets这样的话就是同步处理每一条
               process(conn);
            }
        };
    }

    private Handler<Buffer> statistics() {
        return b -> {
            byte fixHeader = b.getByte(0);
            if((fixHeader & 0xf0) != CS.FrameType.PING
                && (fixHeader & 0x60) != CS.FrameType.RECVACK) {
                yutakNetServer.status.inboundMessages.getAndIncrement();
            }
        };
    }
    // can run in event loop
    private Handler<Promise<ConnAckPacket>> connect(NetSocket s,ConnectPacket connectPacket) {
        return promise -> {
            // auth blocking execute !!!
                    // verify client key
                    if (StringKit.same(connectPacket.clientKey, "")) {
                        promise.fail("client key is empty");
                        return;
                    }
                    // default salve
                    // verify token
                    if (StringKit.same(options.managerCount.UID, connectPacket.clientKey)) {
                        if (options.managerCount.on && !StringKit.same(options.managerCount.token, connectPacket.token)) {
//                                    log.error("connect token not true");
                            promise.fail("client token is empty");
                            return;
                        }
                    } else if (options.tokenAuthOn) {
                        if (StringKit.same(connectPacket.token, "")) {
//                                    log.error("token empty");
                            promise.fail("token is empty");
                            return;
                        }

//                        String userToken = store.getUserToken(connectPacket.UID, connectPacket.deviceFlag);
//                        yutakStore.getUserToken(connectPacket.UID,connectPacket.deviceFlag);
//                        byte level = store.getUserDeviceLevel(connectPacket.UID, connectPacket.deviceFlag);
                        if (StringKit.same(null, connectPacket.token)) {
//                                    log.error("token not same");
                            promise.fail("client token is empty");
                            return;
                        }
                        // TODO  :  think here how to use rocksDb api to rebuild , async or sync ?
                    }
                    // check user status
                    ChannelInfo channel = null;
                    yutakStore.getChannelAsync(connectPacket.UID, CS.ChannelType.Person).whenComplete((res,e)->{
                        if (e != null) {
                            log.error("user {} get channel error{}",connectPacket.UID ,e.getMessage());
                            promise.fail(e);
                            return;
                        }
                        boolean ban = false;
                        if (res != null) {
                            ban = res.ban == 1;
                        }
                        if (ban) {
                            promise.fail("user channel is baned");
                            return;
                        }
                        // TODO  :  this security need more!!! 中间加密的一步没做呢！！！ aesIV...
                        List<String> pair = SecurityKit.getPair();

                        // process device
                        List<Conn> oldConns = connectManager.getConnectWithDeviceFlag(connectPacket.UID, connectPacket.deviceFlag);
                        if (oldConns != null && oldConns.size() > 0) {
                            if (connectPacket.deviceFlag == CS.Device.Level.master) {
                                // remove old device
                                oldConns.forEach(conn -> {
                                    connectManager.removeConnect(conn.id);
                                    // send disConnect packet to device which has been disConnect
                                    if (StringKit.diff(conn.deviceID, connectPacket.deviceID)) {
                                        log.debug("remove user {} old conn", conn.uid);
                                        DisConnectPacket p = new DisConnectPacket();
                                        p.reasonCode = CS.ReasonCode.ConnectKick;
                                        p.reason = "login in other device";
                                        // send disconnect packet
                                        conn.netSocket.write(p.encode());
                                    }
                                    conn.close();
                                });
                                // slave service,just remove same device
                            } else if (connectPacket.deviceFlag == CS.Device.Level.slave) {
                                oldConns.forEach(conn -> {
                                    if (StringKit.same(conn.deviceID, connectPacket.deviceID)) {
                                        log.debug("remove user {} slave conn",conn.uid);
                                        connectManager.removeConnect(conn.id);
                                        System.out.println(connectManager.getOnlineCount());
                                        conn.close();
                                    }
                                });
                            }
                        }
                        // build Conn
                        Conn conn = new Conn(SocketKit.ipToLong(s.remoteAddress().host()), s.remoteAddress().host(), s);
                        conn.auth = true;
                        conn.deviceFlag = connectPacket.deviceFlag;
                        conn.deviceID = connectPacket.deviceID;
                        conn.uid = connectPacket.UID;
                        conn.deviceLevel = connectPacket.deviceFlag;
                        conn.maxIdle = options.maxIdle;
                        // add conn in memory
                        connectManager.addConnect(conn);

                        ConnAckPacket p = new ConnAckPacket();
                        BufferKit.encodeFixHeader(p);
                        p.salt = "别看了，赶紧补上";
                        p.serverKey = "...";
                        p.reasonCode = CS.ReasonCode.success;
                        p.timeDiff = 123;
                        p.serverVersion = 1;
                        p.hasServerVersion = 1;
                        // TODO  :  webhook
                        // call back build connect
                        promise.complete(p);
                    });
                };
    }
    private void process(Conn conn) {
        List<Packet> packets = conn.packets;
        // TODO  :  处里完的packet 不需要删除吗？！
        List<Packet> tmpPackets = new ArrayList<>();
        // according type to group packets
        for (int i = 0; i < types.length; i++) {
            tmpPackets.clear();
            for (int j = 0; j < packets.size(); j++) {
                if (packets.get(j).frameType == types[i]) {
                    tmpPackets.add(packets.get(j));
                }
            }
            switch(i) {
                // core process layer
                case 1: processMsgs(conn,tmpPackets.stream().map(t->(SendPacket)t).toList());
                case 0: pingProcess(conn);
                case 2: recvAckProcess(conn,tmpPackets.stream().map(t->(RecvPacket)t).toList());
                case 3: subProcess(conn,tmpPackets.stream().map(t->(SubPacket)t).toList());
            }
        }
    }
    private void processMsgs(Conn conn,List<SendPacket> packets) {

        HashMap<String, List<SendPacket>> channelSendPacketMap = new HashMap<>();
        // split sendPacket by channel
        for (SendPacket p : packets) {
            String channelKey = SocketKit.buildK(p.channelID,p.channelType);
            List<SendPacket> channelSendPackets = channelSendPacketMap.get(channelKey);
            if (channelSendPackets == null) {channelSendPackets = new ArrayList<>();}
            channelSendPackets.add(p);
            channelSendPacketMap.put(channelKey, channelSendPackets);
        }

        channelSendPacketMap.forEach((k, v) -> {

            CommonChannel channel = channelManager.getChannel(v.get(0).channelID, v.get(0).channelType);
            if (channel != null) {
                // in memory
                processChannel(conn,channel).handle(v);
            }else {
                // load from store
                channelManager.getChannelAsync(v.get(0).channelID,v.get(0).channelType).whenComplete((r,e)->{
                    if (e != null) {
                        log.error("load channel error:{},channelID:{}.channelType:{}", e.getMessage(),v.get(0).channelID, v.get(0).channelType);
                        return;
                    }
                    if (r != null ) {
                        processChannel(conn,r).handle(v);
                    }
                });
            }
        });
    }

        // process delivery packet,message

        // response sendAck Packet
//        deliveryManager.dataOut(conn,sendAckPackets);
    private void subProcess(Conn conn,List<SubPacket> packets) {

    }
    private void recvAckProcess(Conn conn,List<RecvPacket> recvPackets) {

    }
    // process same type packets
    private Handler<List<SendPacket>> processChannel(Conn conn,CommonChannel channel) {
        return packets-> {
            // simple data out
            if (channel == null) {
                deliveryManager.dataOut(conn,buildAck(CS.ReasonCode.ChannelNotExist, packets));
                return;
            }
            if(channel.baned()) {
                deliveryManager.dataOut(conn,buildAck(CS.ReasonCode.Ban, packets));
            } else {
                deliveryManager.dataOut(conn,buildAck(CS.ReasonCode.success, packets));
            }
            //
            deliveryManager.delivery(conn,packets).handle(channel);
        };
    }

    private List<SendAckPacket> buildAck(byte code,List<SendPacket> ss) {
        ArrayList<SendAckPacket> packets = new ArrayList<>();
        for (SendPacket se : ss) {
            SendAckPacket p = new SendAckPacket();
            p.dup = se.dup;
            p.frameType = CS.FrameType.SENDACK;
            p.noPersist = se.noPersist;
            p.redDot = se.redDot;
            p.syncOnce = se.syncOnce;
            p.hasServerVersion = se.hasServerVersion;
            p.reasonCode = code;
            p.clientSeq = se.clientSeq;
            p.clientMsgNo = se.clientMsgNo;
            packets.add(p);
        }
        return packets;
    }
    private void pingProcess(Conn conn) {
        // Ping packet 池化技术！
        PongPacket p = new PongPacket();
        p.frameType = CS.FrameType.PONG;
        Buffer b = p.encode();
        conn.netSocket.write(b);
    }
}
