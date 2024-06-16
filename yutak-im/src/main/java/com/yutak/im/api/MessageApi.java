package com.yutak.im.api;

import com.yutak.im.core.ChannelManager;
import com.yutak.im.core.DeliveryManager;
import com.yutak.im.core.YutakNetServer;
import com.yutak.im.domain.*;
import com.yutak.im.proto.CS;
import com.yutak.im.proto.RecvPacket;
import com.yutak.im.store.H2Store;
import com.yutak.im.store.Store;
import com.yutak.vertx.anno.RouteHandler;
import com.yutak.vertx.anno.RouteMapping;
import com.yutak.vertx.core.HttpMethod;
import com.yutak.vertx.kit.ReqKit;
import com.yutak.vertx.kit.ResKit;
import com.yutak.vertx.kit.UUIDKit;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.List;

@RouteHandler("/message")
public class MessageApi {
    private final DeliveryManager deliveryManager;
    private final ChannelManager channelManager;
//    private final Store store;
    public MessageApi() {
        channelManager = ChannelManager.get();
        deliveryManager = DeliveryManager.get();
//        store = H2Store.get();
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
            // if channelID == null && subscribers.size() > 0, means need to create a tmp channel
            String channelID = s.channelID;
            byte channelType = s.channelType;
            if((channelID == null || channelID.isEmpty() )&& s.subscribers.size() > 0)  {
                // need to create tmp channel
                channelID = channelManager.tmpChannelPrefix + UUIDKit.get();
                channelType = CS.ChannelType.Group;
                channelManager.createTmpChannel(channelID,channelType,s.subscribers);
            }
            String clientMsgNo = s.clientMsgNo;

            if(clientMsgNo == null || clientMsgNo.isEmpty()) {
                clientMsgNo = UUIDKit.get();
            }
            // send message

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
//            Stream.Meta streamMeta = store.getStreamMeta(s.channelId, s.channelType, s.streamNo);
//            if(streamMeta == null) {
//                ResKit.error(ctx,"no invalid stream info");
//                return;
//            }
//            streamMeta.streamFlag = CS.Stream.end;
//            store.saveStreamMeta(streamMeta);
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
            // TODO  : 消息存储问题，这里是否需要redis优化
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
}

