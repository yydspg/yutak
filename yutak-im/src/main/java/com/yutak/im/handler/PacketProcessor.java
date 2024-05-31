package com.yutak.im.handler;

import com.yutak.im.core.ConnectManager;
import com.yutak.im.core.Options;
import com.yutak.im.core.YutakNetServer;
import com.yutak.im.domain.Conn;
import com.yutak.im.kit.BufferKit;
import com.yutak.im.kit.SecurityKit;
import com.yutak.im.proto.*;
import com.yutak.im.store.Store;
import com.yutak.vertx.kit.StringKit;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class PacketProcessor {
    private ConnectManager connectManager;
    private Options options;
    private Store store;
    private final Logger log;
    private final YutakNetServer yutakNetServer;
    // id 生成器
    private final AtomicLong idGenerator = new AtomicLong(0);
    private Vertx vertx;

    private PacketProcessor() {
        yutakNetServer = YutakNetServer.get();
        log = LoggerFactory.getLogger(this.getClass());
        connectManager = new ConnectManager();
    }

    private final static PacketProcessor instance = new PacketProcessor();

    public static PacketProcessor get() {
        return instance;
    }

    public Handler<Buffer> statistics() {
        return b -> {
            byte fixHeader = b.getByte(0);
            if((fixHeader & 0xf0) != CS.FrameType.PING) {
                yutakNetServer.status.inboundMessages.getAndIncrement();
            }
        };
    }

    public Handler<Buffer> packet(NetSocket s) {
        return b -> {
            log.info("packet send");
            // decode layer
            Packet packet = BufferKit.decodePacket(b);
            // check data packet,if return null means decode fail
            if (packet == null) {
                s.end();
                return;
            }
            // process packet
            if (packet.frameType == CS.FrameType.CONNECT) {
                // connect status build
                final ConnectPacket connectPacket = (ConnectPacket) packet;
                // auth blocking execute !!!
                vertx.executeBlocking(promise -> {
                    // verify client key
                    if (StringKit.same(connectPacket.clientKey, "")) {
                        promise.fail("client key is empty");
                        return;
                    }
                    // default salve
                    byte deviceLevel = CS.Device.Level.slave;
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
                        // TODO  :  构建一个本地的数据库，目前的打算是 h2 使用，用内存模式，以后学习下，现在先把业务写完
                        String userToken = store.getUserToken(connectPacket.UID, connectPacket.deviceFlag);
                        byte level = store.getUserDeviceLevel(connectPacket.UID, connectPacket.deviceFlag);
                        if (StringKit.same(userToken, connectPacket.token)) {
//                                    log.error("token not same");
                            promise.fail("client token is empty");
                            return;
                        }
                        deviceLevel = level;
                    } else {
                        deviceLevel = CS.Device.Level.slave;
                    }
                    // check user status
                    Store.ChannelInfo channel = store.getChannel(connectPacket.UID, CS.ChannelType.Person);

                    if (channel == null) {
                        return;
                    }
                    if (channel.ban) {
//                                log.error("user status ban");
                        promise.fail("client ban");
                        return;
                    }
                    // TODO  :  this security need more!!! 中间加密的一步没做呢！！！ aesIV...
                    List<String> pair = SecurityKit.getPair();

                    // process device
                    List<Conn> oldConns = connectManager.getConnectWithDeviceFlag(connectPacket.UID, connectPacket.deviceFlag);
                    if (oldConns.size() > 0) {
                        if (deviceLevel == CS.Device.Level.master) {
                            // remove old device
                            oldConns.forEach(conn -> {
                                connectManager.removeConnect(conn.id);
                                // send disConnect packet to device which has been disConnect
                                if (StringKit.diff(conn.deviceID, connectPacket.deviceID)) {
                                    log.info("remove old conn");
                                    DisConnectPacket p = new DisConnectPacket();
                                    p.reasonCode = CS.ReasonCode.ConnectKick;
                                    p.reason = "login in other device";
                                    // send disconnect packet
                                    conn.netSocket.write(p.encode());
                                }
                                conn.close();
                            });
                            // slave service,just remove same device
                        } else if (deviceLevel == CS.Device.Level.slave) {
                            oldConns.forEach(conn -> {
                                if (StringKit.same(conn.deviceID, connectPacket.deviceID)) {
                                    log.info("remove slave conn");
                                    connectManager.removeConnect(conn.id);
                                    conn.close();
                                }
                            });
                        }
                    }
                    // build Conn
                    Conn conn = new Conn(idGenerator.get(), s.remoteAddress().host(), s);
                    conn.auth = true;
                    conn.deviceFlag = connectPacket.deviceFlag;
                    conn.deviceID = connectPacket.deviceID;
                    conn.uid = connectPacket.UID;
                    conn.deviceLevel = deviceLevel;
                    conn.maxIdle = options.maxIdle;
                    // add conn
                    connectManager.addConnect(conn);

                    ConnAckPacket p = new ConnAckPacket();
                    BufferKit.encodeFixHeader(p);
                    p.salt = "别看了，赶紧补上";
                    p.serverKey = "...";
                    p.reasonCode = CS.ReasonCode.success;
                    p.timeDiff = 123;
                    p.serverVersion = 1;
                    p.hasServerVersion = true;
                    // TODO  :  webhook
                    // call back build connect
                    promise.complete(p);
                }).onComplete(res -> {
                    //connect success
                    if (res.succeeded() && res.result() != null) {
                        s.write(((ConnectPacket) res.result()).encode());
                        return;
                    }
                    // connect fail
                    log.error(res.cause().getMessage());
                    s.end();
                });
            // other packet
            } else {
                switch(packet.frameType) {
                    case CS.FrameType.PING -> pingProcess(packet,s);
                    case CS.FrameType.RECVACK -> recvAckProcess(packet,s);
                    case CS.FrameType.SUB -> subProcess(packet,s);
                    case CS.FrameType.SEND -> sendProcess(packet,s);
                }
            }
        };
    }
    private void sendProcess(Packet packet,NetSocket s) {}
    private void subProcess(Packet packet,NetSocket s) {}
    private void recvAckProcess(Packet packet,NetSocket s) {

    }
    private void pingProcess(Packet packet,NetSocket s) {
        // Ping packet 池化技术！
        PongPacket p = new PongPacket();
        p.frameType = CS.FrameType.PONG;
        Buffer b = p.encode();
        s.write(b);
    }
}
