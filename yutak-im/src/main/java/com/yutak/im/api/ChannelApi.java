package com.yutak.im.api;

import com.yutak.im.core.ChannelManager;
import com.yutak.im.domain.CommonChannel;
import com.yutak.im.domain.Req;
import com.yutak.im.proto.CS;
import com.yutak.im.store.ChannelInfo;
import com.yutak.im.store.YutakStore;
import com.yutak.vertx.anno.RouteHandler;
import com.yutak.vertx.anno.RouteMapping;
import com.yutak.vertx.core.HttpMethod;
import com.yutak.vertx.kit.ReqKit;
import com.yutak.vertx.kit.ResKit;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


@RouteHandler("/channel")
public class ChannelApi {
    private static final Logger log = LoggerFactory.getLogger(ChannelApi.class);
    private ChannelManager channelManager;
    private YutakStore yutakStore;
    public ChannelApi() {
        channelManager = ChannelManager.get();
        yutakStore = YutakStore.get();
    }
    @RouteMapping(path = "/create",method = HttpMethod.POST)
    public Handler<RoutingContext> create() {
        return ctx -> {
            Req.ChannelCreate c = ReqKit.getObjectInBody(ctx, Req.ChannelCreate.class);
            if (c == null) {
                ResKit.error(ctx,"no  data info");
                return;
            }
            if(c.channelInfo.channelId == null || c.channelInfo.channelId.isEmpty()) {
                ResKit.error(ctx,"channelId is null");
                return;
            }
            if(c.channelInfo.channelType == CS.ChannelType.Person) {
                ResKit.error(ctx,"channelType not support person");
                return;
            }
            // TODO  :  here should be sync
            // add
//            store.addOrUpdateChannel(c.channelInfo);
            yutakStore.addOrUpdateChannel(c.channelInfo);
            // if update channel ,need all subscribers
//            store.removeAllSubscribers(c.channelInfo.channelId,c.channelInfo.channelType);
            yutakStore.removeAllSubscribers(c.channelInfo.channelId,c.channelInfo.channelType);
            // add subscribers
            if(c.subscribers != null && !c.subscribers.isEmpty()) {
//                store.addSubscribers(c.channelInfo.channelId,c.channelInfo.channelType,c.subscribers);
                yutakStore.addSubscribers(c.channelInfo.channelId,c.channelInfo.channelType,c.subscribers);
            }
            channelManager.deleteChannelCache(c.channelInfo.channelId,c.channelInfo.channelType);
            ResKit.success(ctx);
        };
    }
    // query channel info
    @RouteMapping(path = "/info",method = HttpMethod.POST)
    public Handler<RoutingContext> info() {
        return ctx -> {
            ChannelInfo c = ReqKit.getObjectInBody(ctx, ChannelInfo.class);
            if (c == null) {
                ResKit.error(ctx,"no  data info");
                return ;
            }
//            store.addOrUpdateChannel(c);
            yutakStore.addOrUpdateChannel(c);
            // update info
            channelManager.getChannelAsync(c.channelId, c.channelType).whenComplete((channel, err) -> {
                if(err != null) {
                    log.error("get channel error:{}", err.getMessage());
                    return;
                }
                if(channel == null) {
                    ResKit.error(ctx,"channel is null");
                    return;
                }
                channel.large = c.large;
                channel.ban = c.ban;
                channel.disband = c.disband;
                ResKit.success(ctx,channel);
            });
        };
    }
    @RouteMapping(path = "/delete",method = HttpMethod.POST)
    public Handler<RoutingContext> delete() {
        return ctx -> {
            JsonObject json = ReqKit.getJSON(ctx);
            if(json == null) {
                ResKit.error(ctx,"no  data info");
                return;
            }
            String channelId = json.getString("channelId");
            byte channelType = Byte.parseByte(json.getString("channelType"));

            if(channelId == null || channelId.isEmpty()) {
                ResKit.error(ctx,"channelId is null");
                return;
            }
            if (channelType == CS.ChannelType.Person) {
                ResKit.error(ctx,"channelType not support person");
                return;
            }
            // persistence

            yutakStore.getChannelAsync(channelId, channelType).whenComplete((info, err) -> {
                if(err != null) {
                    log.error("get channel error:{}", err.getMessage());
                    return;
                }
                if(info == null) {
                    ResKit.success(ctx);
                    return;
                }
                info.ban = 1;
//            store.addOrUpdateChannel(info);
//            store.removeAllSubscribers(channelId, channelType);
                yutakStore.addOrUpdateChannel(info);
                yutakStore.removeAllSubscribers(channelId, channelType);
                // sync cache
                channelManager.deleteChannelCache(channelId,channelType);
                ResKit.success(ctx);
            });
        };
    }
    @RouteMapping(path = "/subscriber/add",method = HttpMethod.POST)
    public Handler<RoutingContext> addSubscriber() {
        return ctx -> {
            Req.AddSubscriber a = ReqKit.getObjectInBody(ctx, Req.AddSubscriber.class);
            if (a == null) {
                ResKit.error(ctx,"no  data info");
                return;
            }
            if(a.channelId == null || a.channelId.isEmpty()) {
                ResKit.error(ctx,"channelId is null");
                return;
            }
            if (a.channelType == CS.ChannelType.Person ) {
                ResKit.error(ctx,"Person channelType not support addSubscriber");
                return;
            }
            // default type is group
            if(a.channelType == 0) {a.channelType = CS.ChannelType.Group;}

             channelManager.getChannelAsync(a.channelId, a.channelType).whenComplete((channel,e)->{
                 if(e != null) {
                     ResKit.error(ctx,"get channel error");
                     return;
                 }
                 if(channel == null) {
                     ResKit.error(ctx,"channel is null");
                     return;
                 }
                 // add tmp subscribers
                 if(a.temp == 1) {
                     if(a.reset == 1) channel.removeAllTmpSubscriber();
                     channel.addTmpSubscriber(a.subscribers);
                 } else {
                     // add real subscribers
                     if (a.reset == 1) {
                         channel.removeAllSubscriber();
//                    store.removeAllSubscribers(a.channelId,a.channelType);
                         yutakStore.removeAllSubscribers(a.channelId, a.channelType);
                     }
                     ArrayList<String> newSubscribers = new ArrayList<>();
                     // build new subscribers
                     a.subscribers.forEach(s -> {
                         if (channel.addSubscriber(s)) newSubscribers.add(s);
                     });
//                store.addSubscribers(a.channelId,a.channelType,newSubscribers);
                    yutakStore.addSubscribers(a.channelId, a.channelType, newSubscribers);
                 }
                 ResKit.success(ctx);
             });
        };
    }
    @RouteMapping(path = "/subscriber/del",method = HttpMethod.POST)
    public Handler<RoutingContext> delSubscriber() {
        return ctx -> {
            Req.RemoveSubscriber r = ReqKit.getObjectInBody(ctx, Req.RemoveSubscriber.class);
            if (r == null) {
                ResKit.error(ctx,"no  data info");
                return;
            }
            if(r.channelId == null || r.channelId.isEmpty()) {
                ResKit.error(ctx,"channelId is null");
                return;
            }
            channelManager.getChannelAsync(r.channelId, r.channelType).whenComplete((channel,e)->{
                if(e != null) {
                    ResKit.error(ctx,"get channel error");
                    return;
                }
                if(channel == null) {
                    ResKit.error(ctx,"channel is null");
                    return;
                }
                if(r.temp == 1) {
                    channel.removeAllTmpSubscriber();
                } else {
                    yutakStore.removeSubscribers(r.channelId,r.channelType,r.subscribers);
                    channel.removeSubscribers(r.subscribers);
                    // TODO  :  current conversation need to be deleted
                }
                ResKit.success(ctx);
            });
        };
    }
    @RouteMapping(path = "/block/add",method = HttpMethod.POST)
    public Handler<RoutingContext> addBlock() {
        return ctx -> {
            Req.BlockList b = ReqKit.getObjectInBody(ctx, Req.BlockList.class);
            if (b == null) {
                ResKit.error(ctx,"no  data info");
                return;
            }
            if(b.channelId == null || b.channelId.isEmpty()) {
                ResKit.error(ctx,"channelId is null");
                return;
            }
            if(b.uids == null || b.uids.isEmpty()) {
                ResKit.error(ctx,"uids is null");
                return;
            }
            channelManager.getChannelAsync(b.channelId, b.channelType).whenComplete((channel,e)->{
                if(e != null) {
                    ResKit.error(ctx,"get channel error");
                    return;
                }
                if(channel == null) {
                    ResKit.error(ctx,"channel is null");
                    return;
                }
                yutakStore.addDenyList(b.channelId,b.channelType,b.uids);
                channel.addBlockList(b.uids);
                ResKit.success(ctx);
            });
        };
    }
    @RouteMapping(path = "/block/set",method = HttpMethod.POST)
    public Handler<RoutingContext> setBlock() {
        return ctx -> {
            Req.BlockList b = ReqKit.getObjectInBody(ctx, Req.BlockList.class);
            if (b == null) {
                ResKit.error(ctx,"no  data info");
                return;
            }
            if(b.channelId == null || b.channelId.isEmpty()) {
                ResKit.error(ctx,"channelId is null");
                return;
            }
            if(b.uids == null || b.uids.isEmpty()) {
                ResKit.error(ctx,"uids is null");
                return;
            }
            channelManager.getChannelAsync(b.channelId, b.channelType).whenComplete((channel,e)-> {
                if (e != null ){
                    ResKit.error(ctx,"get channel error");
                    return;
                }
                if (channel == null) {
                    ResKit.error(ctx, "channel is null");
                    return;
                }
                yutakStore.removeAllDenyList(b.channelId, b.channelType);
                yutakStore.addDenyList(b.channelId, b.channelType, b.uids);
                channel.removeAllBlockList();
                channel.addBlockList(b.uids);
                ResKit.success(ctx);
            });
        };
    }
    @RouteMapping(path = "/block/remove",method = HttpMethod.POST)
    public Handler<RoutingContext> removeBlock() {
        return ctx -> {
            Req.BlockList b = ReqKit.getObjectInBody(ctx, Req.BlockList.class);
            if (b == null) {
                ResKit.error(ctx,"no  data info");
                return;
            }
            if(b.channelId == null || b.channelId.isEmpty()) {
                ResKit.error(ctx,"channelId is null");
                return;
            }
            if(b.uids == null || b.uids.isEmpty()) {
                ResKit.error(ctx,"uids is null");
                return;
            }
            CommonChannel channel = channelManager.getChannel(b.channelId, b.channelType);
            if(channel == null) {
                ResKit.error(ctx,"channel is null");
                return;
            }
            yutakStore.removeDenyList(b.channelId,b.channelType,b.uids);
            channel.removeBlockList(b.uids);
            ResKit.success(ctx);
        };
    }
    @RouteMapping(path = "/white/add",method = HttpMethod.POST)
    public Handler<RoutingContext> addWhite() {
        return ctx -> {
            Req.WhiteList w = ReqKit.getObjectInBody(ctx, Req.WhiteList.class);
            if (w == null) {
                ResKit.error(ctx,"no  data info");
                return;
            }
            if(w.channelId == null || w.channelId.isEmpty()) {
                ResKit.error(ctx,"channelId is null");
                return;
            }
            if(w.uids == null || w.uids.isEmpty()) {
                ResKit.error(ctx,"uids is null");
                return;
            }
            channelManager.getChannelAsync(w.channelId, w.channelType).whenComplete((channel,e)->{
                if (e != null ){
                    ResKit.error(ctx,"get channel error");
                }
                if(channel == null) {
                    ResKit.error(ctx,"channel is null");
                    return;
                }
                yutakStore.addAllowList(w.channelId, w.channelType,w.uids);
                channel.addWhiteList(w.uids);
                ResKit.success(ctx);
            });
        };
    }
    @RouteMapping(path = "/white/remove",method = HttpMethod.POST)
    public Handler<RoutingContext> removeWhite() {
        return ctx -> {
            Req.WhiteList w = ReqKit.getObjectInBody(ctx, Req.WhiteList.class);
            if (w == null) {
                ResKit.error(ctx,"no  data info");
                return;
            }
            if(w.channelId == null || w.channelId.isEmpty()) {
                ResKit.error(ctx,"channelId is null");
                return;
            }
            if(w.uids == null || w.uids.isEmpty()) {
                ResKit.error(ctx,"uids is null");
                return;
            }
            channelManager.getChannelAsync(w.channelId, w.channelType).whenComplete((channel,e)-> {
                if (e != null) {
                    ResKit.error(ctx,"get channel error");
                    return;
                }
                if (channel == null) {
                    ResKit.error(ctx, "channel is null");
                    return;
                }
                yutakStore.removeAllowList(w.channelId, w.channelType, w.uids);
                channel.removeWhiteList(w.uids);
                ResKit.success(ctx);
            });
        };
    }
    @RouteMapping(path = "/white/set",method = HttpMethod.POST)
    public Handler<RoutingContext> setWhite() {
        return ctx -> {
            Req.WhiteList w = ReqKit.getObjectInBody(ctx, Req.WhiteList.class);
            if (w == null) {
                ResKit.error(ctx,"no  data info");
                return;
            }
            if(w.channelId == null || w.channelId.isEmpty()) {
                ResKit.error(ctx,"channelId is null");
                return;
            }
            if(w.uids == null || w.uids.isEmpty()) {
                ResKit.error(ctx,"uids is null");
                return;
            }
            channelManager.getChannelAsync(w.channelId, w.channelType).whenComplete((channel,e)-> {
                if (e != null) {
                    ResKit.error(ctx,"get channel error");
                    return;
                }
                if (channel == null) {
                    ResKit.error(ctx, "channel is null");
                    return;
                }
                yutakStore.removeAllAllowList(w.channelId, w.channelType);
                yutakStore.addAllowList(w.channelId, w.channelType, w.uids);
                channel.removeAllWhiteList();
                channel.addWhiteList(w.uids);
                ResKit.success(ctx);
            });
        };
    }
    @RouteMapping(path = "/white/list",method = HttpMethod.GET)
    public Handler<RoutingContext> listWhite() {
        return ctx -> {
            String channelId = ReqKit.getStrInPath(ctx, "channelId");
            if(channelId == null || channelId.isEmpty()) {
                ResKit.error(ctx,"channelId is null");
                return;
            }
            channelManager.getChannelAsync(channelId, CS.ChannelType.Group).whenComplete((channel,e)->{
                if (e != null) {
                    ResKit.error(ctx,"get channel error");
                    return;
                }
                if (channel == null) {
                    ResKit.error(ctx, "channel is null");
                    return;
                }
                List<String> whiteList = channel.getWhiteList();
                if(whiteList == null || whiteList.isEmpty()) {
                    ResKit.error(ctx,"whiteList is null");
                    return;
                }
                JsonArray o = new JsonArray();
                whiteList.forEach(o::add);
                ResKit.JSON(ctx,200,o);
            });
        };
    }
    @RouteMapping(path = "/syncMessage",method = HttpMethod.POST)
    public Handler<RoutingContext> syncMessage() {
        return ctx -> {

        };
    }

    public static void main(String[] args) {
        Req.ChannelCreate c = new Req.ChannelCreate();
        c.channelInfo = new ChannelInfo();
        c.channelInfo.channelType = 1;
        c.channelInfo.channelId = "123";
        c.subscribers = new ArrayList<>();
        c.subscribers.add("dfawdf");
        c.subscribers.add("asfew");
        JsonObject json = new JsonObject();
//        json = JsonObject.mapFrom(c);
        String jsonString = "{\"channelInfo\":{\"channelId\":\"123\",\"channel\":\"Test Channel\"},\"subscribers\":[\"user1\",\"user2\"]}";


        // 从JSON字符串解析回ChannelCreate对象
        String encode = Json.encode(c);
        System.out.println(encode);
        Req.ChannelCreate channelCreate = Json.decodeValue(encode, c.getClass());
        System.out.println(channelCreate);

    }
}
