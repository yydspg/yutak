package com.yutak.im.api;

import com.yutak.im.core.ChannelManager;
import com.yutak.im.core.DeliveryManager;
import com.yutak.im.core.YutakNetServer;
import com.yutak.im.domain.Message;
import com.yutak.im.domain.Req;
import com.yutak.im.domain.Res;
import com.yutak.im.proto.CS;
import com.yutak.im.proto.RecvPacket;
import com.yutak.im.store.YutakStore;
import com.yutak.vertx.anno.RouteHandler;
import com.yutak.vertx.anno.RouteMapping;
import com.yutak.vertx.core.HttpMethod;
import com.yutak.vertx.kit.ReqKit;
import com.yutak.vertx.kit.ResKit;
import com.yutak.vertx.kit.StringKit;
import com.yutak.vertx.kit.UUIDKit;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RouteHandler("/message")
public class MessageApi {
    private static final Logger log = LoggerFactory.getLogger(MessageApi.class);
    private final DeliveryManager deliveryManager;
    private final ChannelManager channelManager;
    private final YutakStore yutakStore;
    private final YutakNetServer yutakNetServer;
    public MessageApi() {
        channelManager = ChannelManager.get();
        deliveryManager = DeliveryManager.get();
        yutakStore = YutakStore.get();
        yutakNetServer = YutakNetServer.get();
    }

    // send message
    @RouteMapping(path = "/send",method = HttpMethod.POST)
    public Handler<RoutingContext> send() {
        return ctx -> {
            Req.SendMessage s = ReqKit.getObjectInBody(ctx, Req.SendMessage.class);
            if(s == null) {
                ResKit.error(ctx,"no invalid data info");
                return;
            }
            // check json info
            // if channelID == null && subscribers.size() > 0, means need to create a tmp channel
            String channelID = s.channelID;
            byte channelType = s.channelType;
            if((channelID == null || channelID.isEmpty() )&& s.subscribers.size() > 0)  {
                // need to create tmp channel
                channelID = channelManager.tmpChannelPrefix + UUIDKit.get();
                channelType = CS.ChannelType.Group;
                // sync
                channelManager.createTmpChannel(channelID,channelType,s.subscribers);
            }
            String clientMsgNo = s.clientMsgNo;

            if(clientMsgNo == null || clientMsgNo.isEmpty()) {
                clientMsgNo = UUIDKit.get();
            }
            String tmp = clientMsgNo;
            // send message to channel
            Future.future(sendMsgChannel(s,channelID,channelType,clientMsgNo,CS.Stream.ing)).onComplete(ar -> {
                if(ar.succeeded()) {
                    JsonObject o = new JsonObject(ar.result());
                    o.put("clientMsgNo",tmp);
                    ResKit.JSON(ctx,200,o);
                    return;
                }else {
                    ResKit.error(ctx,ar.cause().getMessage());
                }
            });
        };
    }
    // send batch message
    @RouteMapping(path = "/sendBatch",method = HttpMethod.POST)
    public Handler<RoutingContext> sendBatch() {
        return ctx -> {

        };
    }
    // msg sync
    @RouteMapping(path = "/sync",method = HttpMethod.POST)
    public Handler<RoutingContext> sync() {
        return ctx -> {
            Req.Sync s = ReqKit.getObjectInBody(ctx, Req.Sync.class);
            if(s == null) {
                ResKit.error(ctx,"no invalid data info");
                return;
            }
            if (s.uid == null || s.uid.isEmpty()) {
                ResKit.error(ctx,"no invalid data info");
                return;
            }
//            int readIndex = store.getMessageOfUserCursor(s.uid);
//            int startIndex = s.messageSeq;
//            if(startIndex <= 0) {
//                startIndex = readIndex;
//            } else if (startIndex < readIndex) {
//                startIndex = readIndex;
//            } else startIndex = readIndex;

//            startIndex ++;
//            List<Message> messages = store.syncMessageOfUser(s.uid, startIndex, s.limit);
//            if (messages == null || messages.isEmpty()) {
//                ResKit.error(ctx,"no messages found");
//                return;
//            }
            // response
            ArrayList<Res.Msg> msgs = new ArrayList<>();
//            if(messages != null && messages.size() > 0) {
//                messages.forEach(m -> {
//                    Res.Msg msg = new Res.Msg();
//                    msgs.add(msg.build(m));
//                });
//            }
            ResKit.JSON(ctx,200, Json.encode(msgs));
        };
    }
    // msg sync ack
    @RouteMapping(path = "/syncAck",method = HttpMethod.POST)
    public Handler<RoutingContext> syncAck() {
        return ctx -> {
            Req.SyncAck s = ReqKit.getObjectInBody(ctx, Req.SyncAck.class);
            if(s == null) {
                ResKit.error(ctx,"no invalid data info");
                return;
            }
            if (s.uid == null || s.uid.isEmpty()) {
                ResKit.error(ctx,"uid is empty");
                return;
            }
//            store.updateMessageOfUserCursorNeed(s.uid,s.lastMessageSeq);
            ResKit.success(ctx);
        };
    }
    // stream send msg start
    @RouteMapping(path = "/stream/start",method = HttpMethod.POST)
    public Handler<RoutingContext> streamStart() {
        return ctx -> {
            Req.StreamStart s = ReqKit.getObjectInBody(ctx, Req.StreamStart.class);
            if(s == null) {
                ResKit.error(ctx,"no invalid data info");
                return;
            }
            if(s.channelID == null || s.channelID.isEmpty()) {
                ResKit.error(ctx,"no invalid channelID");
                return;
            }
            String clientMsgNo = s.clientMsgNo;
            if(clientMsgNo == null || clientMsgNo.isEmpty()) {
                clientMsgNo = UUIDKit.get();
            }
            String streamNo = UUIDKit.get();
            byte streamType = CS.Stream.start;
            Req.SendMessage m = new Req.SendMessage();
            m.channelID = s.channelID;
            m.payload = s.payload;
            m.clientMsgNo = clientMsgNo;
            m.streamNo = streamNo;
            m.header = s.header;
            m.fromUID = s.fromUID;
            m.channelType = s.channelType;
//            ResKit.success(ctx);

        };
    }
    // stream send msg end
    @RouteMapping(path = "/stream/end",method = HttpMethod.POST)
    public Handler<RoutingContext> streamEnd() {
        return ctx -> {
            Req.StreamEnd s = ReqKit.getObjectInBody(ctx, Req.StreamEnd.class);
            if(s == null) {
                ResKit.error(ctx,"no invalid data info");
                return;
            }
            ResKit.success(ctx);
        };
    }

    // query message
    @RouteMapping(path = "/messages",method = HttpMethod.POST)
    public Handler<RoutingContext> messages() {
        return ctx -> {
            Req.QueryMessage q = ReqKit.getObjectInBody(ctx, Req.QueryMessage.class);
            if(q == null) {
                ResKit.error(ctx,"no invalid data info");
                return;
            }
            if (q.channelId == null || q.channelId.isEmpty()) {
                ResKit.error(ctx,"no invalid channelID");
                return;
            }
            if(q.seqs == null || q.seqs.isEmpty()) {
                ResKit.error(ctx,"no invalid seqs");
                return;
            }
            ArrayList<Message> messages = new ArrayList<>();

            q.seqs.forEach(s->{
//                Message msg = store.loadMessage(q.channelId, q.channelType, s);
//                if(msg != null) {
//                    messages.add(msg);
//                }
            });
            ArrayList<Res.Msg> msgs = new ArrayList<>();
            if (messages.size() > 0) {
                for (Message m : messages) {
                    Res.Msg msg = new Res.Msg();
                    msg.build(m);
                    msgs.add(msg);
                }
            }
            ResKit.success(ctx,msgs);
        };
    }
    //  no blocking code

    private Handler<Promise<Map<String,Object>>> sendMsgChannel(Req.SendMessage req, String channelID, int channelType, String clientMsgNo, int streamFlag) {
        return promise -> {
            // build message ID
            long msgID = yutakNetServer.ID.getAndIncrement();

            // if channel type == person , just build a fake channel
            String fakeChannelID = channelID;
            if(channelType == CS.ChannelType.Person && StringKit.isEmpty(req.fromUID)) {
                fakeChannelID = req.fromUID + "@" + channelID;
            }
            channelManager.getChannelAsync(fakeChannelID,channelType).whenComplete((channel, e) -> {
                if(e != null) {
                    log.error("get channel error {}", e.getMessage());
                    promise.fail(e.getMessage());
                    return;
                }
                if(channel == null) {
                    log.error("no channel found,{}", channelID);
                    promise.fail("no channel found");
                    return;
                }
                // if channel is large ,send message options contains syncOnce and need persist --> not support
                if (channel.large == 1 &&(req.header.syncOnce == 1 && req.header.noPersist == 0)) {
                    log.error("channel {} not support syncOnce {} and noPersist {} ops", channelID, req.header.syncOnce, req.header.noPersist);
                    promise.fail("channel " + channelID + " not support syncOnce " + req.header.noPersist);
                    return;
                }
                // remove redundant element
                HashSet<String> subscribers = new HashSet<>(req.subscribers);

                // set stream setting
                byte setting = 0;
                if (StringKit.isNotEmpty(req.streamNo)) {
                    setting = CS.Setting.stream;
                }
                // build message
                Message msg = buildMessage(req, setting, msgID,clientMsgNo, subscribers);

                List<Message> list = List.of(msg);
                // need persist  do not sync once ,not tmp channel
                if(msg.recvPacket.noPersist == 0 && msg.recvPacket.syncOnce == 0 && !channelManager.isTmpChannel(channelID)) {

                    // stream message
                    if(msg.recvPacket.streamFlag == CS.Stream.ing) {
                        // store the stream item

                    }else {
                        // store message
                    }
                }
                // web hook

                // put message to channel
                Future.future(channel.putMessage(list,subscribers.stream().toList(), req.fromUID,"system", CS.Device.Level.master)).onComplete(m->{
                    if (m.succeeded()) {
                        HashMap<String, Object> o = new HashMap<>();
                        o.put("messageID", msgID);
                        o.put("messageSeq", msg.recvPacket.messageSeq);
                        promise.complete(o);
                    }
                    promise.fail("put message failed");
                });
            });
        };
    }
    public Message buildMessage(Req.SendMessage q, byte s, Long msgID,String clientMsgNo, Set<String> ss) {
        RecvPacket r = new RecvPacket();
        r.msgKey = "";
        r.redDot = q.header.redDot ;
        r.syncOnce = q.header.syncOnce ;
        r.noPersist = q.header.noPersist;
        r.setting = s;
        r.messageID = msgID;
        r.streamNo = q.streamNo == null ? "" : q.streamNo;
        r.fromUID = q.fromUID == null ? "" : q.fromUID;
        r.channelID = q.channelID == null ? "" : q.channelID;
        r.channelType = q.channelType;
        r.expire = q.expire;
        r.topic = "";
        r.clientMsgNo = clientMsgNo;
        r.timestamp = (int) System.currentTimeMillis();
        r.payload = q.payload.getBytes(StandardCharsets.UTF_8);
        Message m = new Message();
        m.recvPacket = r;
        // system channel type
        m.fromDeviceFlag = CS.ChannelType.System;
        m.subscribers = ss.stream().toList();
        return m;
    }
}

