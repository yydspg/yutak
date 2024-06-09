package com.yutak.im.api;

import com.yutak.im.core.ChannelManager;
import com.yutak.im.core.DeliveryManager;
import com.yutak.im.core.Options;
import com.yutak.im.core.YutakNetServer;
import com.yutak.im.domain.CommonChannel;
import com.yutak.im.domain.Message;
import com.yutak.im.domain.Req;
import com.yutak.im.handler.PacketProcessor;
import com.yutak.im.proto.CS;
import com.yutak.im.proto.Packet;
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
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RouteHandler("/message")
public class MessageApi {
    private final DeliveryManager deliveryManager;
    private final ChannelManager channelManager;
    private final Store store;
    public MessageApi() {
        channelManager = ChannelManager.get();
        deliveryManager = DeliveryManager.get();
        store = H2Store.get();
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
            Future.future(sendMsgToChannel(channelID, channelType, s, clientMsgNo, CS.Stream.ing))
                    .onComplete(t->{
                        if(t.succeeded()) {
                            ResKit.JSON(ctx,200,t.result());
                        } else {
                            ResKit.error(ctx,t.cause().getMessage());
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

        };
    }
    // msg sync ack
    @RouteMapping(path = "/syncAck",method = HttpMethod.POST)
    public Handler<RoutingContext> syncAck() {
        return ctx -> {

        };
    }
    // stream send msg start
    @RouteMapping(path = "/stream/start",method = HttpMethod.POST)
    public Handler<RoutingContext> streamStart() {
        return ctx -> {

        };
    }
    // stream send msg end
    @RouteMapping(path = "/stream/end",method = HttpMethod.POST)
    public Handler<RoutingContext> streamEnd() {
        return ctx -> {

        };
    }

    // query message
    @RouteMapping(path = "/messages",method = HttpMethod.POST)
    public Handler<RoutingContext> messages() {
        return ctx -> {

        };
    }
    private Handler<Promise<JsonObject>> sendMsgToChannel(String channelID, byte channelType, Req.SendMessage sendReq, String clientMsgNo, int streamFlag) {

        // TODO  :  monitor
        return promise -> {

            String fakeChannelID = channelID;
            // person type
            if (channelType == CS.ChannelType.Person && sendReq.fromUID != null && !sendReq.fromUID.isEmpty()) {
                fakeChannelID = sendReq.fromUID + "@" + channelID;

            } else {
            // common type

            }
            CommonChannel channel = channelManager.getChannel(channelID, channelType);
            if (channel == null) {
                promise.fail("channel not found");
                return ;
            }
            if (channel.large && sendReq.header.syncOnce && !sendReq.header.noPersist) {
                // not support
                promise.fail("not support sync once in large channel");
                return;
            }
            // distinct
            List<String> subscribers = sendReq.subscribers.stream().distinct().toList();

            // TODO  :  stream

            Message message = new Message();
            message.recvPacket = new RecvPacket();

            message.recvPacket.redDot = sendReq.header.redDot;
            message.recvPacket.syncOnce = sendReq.header.syncOnce;
            message.recvPacket.noPersist = sendReq.header.noPersist;
            message.recvPacket.setting = 0;
            message.recvPacket.messageID = YutakNetServer.get().ID.getAndIncrement();
            message.recvPacket.channelID = channelID;
            message.recvPacket.channelType = channelType;
            message.recvPacket.expire = sendReq.expire;
            message.recvPacket.fromUID = sendReq.fromUID;
            message.recvPacket.timestamp = (int) System.currentTimeMillis();
            message.recvPacket.payload = sendReq.payload;
            message.recvPacket.clientMsgNo = clientMsgNo;

            message.subscribers = subscribers;
            message.fromDeviceFlag = CS.Device.Flag.sys;

            // persistence

            if(!sendReq.header.noPersist && !sendReq.header.syncOnce && !channelManager.isTmpChannel(channelID)) {
                if (message.recvPacket.streamFlag == CS.Stream.ing) {

                } else {
                    store.appendMessage(channelID,channelType,List.of(message));
                }
            }
            // TODO  :  webhook

            // start put message into channel
            deliveryManager.routeMsg(List.of(message),subscribers,null,sendReq.fromUID,"system",CS.Device.Level.master);

            JsonObject e = new JsonObject();
            e.put("message_id",fakeChannelID);
            e.put("clientMsgNo", clientMsgNo);
            e.put("messageSeq",message.recvPacket.messageSeq);
            promise.complete(e);
        };
    }
}

