package com.yutak.im.api;

import com.yutak.im.core.ChannelManager;
import com.yutak.im.domain.Channel;
import com.yutak.im.domain.CommonChannel;
import com.yutak.im.domain.Req;
import com.yutak.im.kit.BufferKit;
import com.yutak.im.proto.CS;
import com.yutak.im.store.Store;
import com.yutak.vertx.anno.RouteHandler;
import com.yutak.vertx.anno.RouteMapping;
import com.yutak.vertx.core.HttpMethod;
import com.yutak.vertx.kit.ReqKit;
import com.yutak.vertx.kit.ResKit;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.List;


@RouteHandler("/channel")
public class ChannelApi {
    private ChannelManager channelManager;
    private Store store;
    @RouteMapping(path = "/create",method = HttpMethod.POST,block = true)
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
            // add
            store.addOrUpdateChannel(c.channelInfo);
            // if update channel ,need all subscribers
            store.removeAllSubscribers(c.channelInfo.channelId,c.channelInfo.channelType);
            // add subscribers
            if(c.subscribers != null && !c.subscribers.isEmpty()) {
                store.addSubscribers(c.channelInfo.channelId,c.channelInfo.channelType,c.subscribers);
            }
            channelManager.deleteChannelCache(c.channelInfo.channelId,c.channelInfo.channelType);
            ResKit.success(ctx);
        };
    }
    // query channel info
    @RouteMapping(path = "/info",method = HttpMethod.POST,block = true)
    public Handler<RoutingContext> info() {
        return ctx -> {
            Store.ChannelInfo c = ReqKit.getObjectInBody(ctx, Store.ChannelInfo.class);
            if (c == null) {
                ResKit.error(ctx,"no  data info");
                return ;
            }
            store.addOrUpdateChannel(c);
            // update info
            if(c.channelType == CS.ChannelType.Group) {
                CommonChannel channel = channelManager.getChannel(c.channelId, c.channelType);
                channel.large = c.large;
                channel.ban = c.ban;
                channel.disband = c.disband;
            }
            ResKit.success(ctx);
        };
    }
    @RouteMapping(path = "/delete",method = HttpMethod.POST,block = true)
    public Handler<RoutingContext> delete() {
        return ctx -> {
            JsonObject json = ReqKit.getJSON(ctx);
            String channelId = json.getString("channelId");
            byte channelType = Byte.parseByte(json.getString("channelType"));

            // persistence
            Store.ChannelInfo info = store.getChannel(channelId, channelType);
            if(info == null) {
                ResKit.success(ctx);
                return;
            }
            info.ban = true;
            store.addOrUpdateChannel(info);
            store.removeAllSubscribers(channelId, channelType);
            // sync cache
            channelManager.deleteChannelCache(channelId,channelType);
            ResKit.success(ctx);
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

            CommonChannel channel = channelManager.getChannel(a.channelId, a.channelType);
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
                if(a.reset == 1) {
                    channel.removeAllSubscriber();
                    store.removeAllSubscribers(a.channelId,a.channelType);
                }
                ArrayList<String> newSubscribers = new ArrayList<>();
                // build new subscribers
                a.subscribers.forEach(s -> {
                    if(channel.addSubscriber(s)) newSubscribers.add(s);
                });
                store.addSubscribers(a.channelId,a.channelType,newSubscribers);
            }
            ResKit.success(ctx);
        };
    }
    @RouteMapping(path = "/subscriber/del",method = HttpMethod.POST,block = true)
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
            CommonChannel channel = channelManager.getChannel(r.channelId, r.channelType);
            if(channel == null) {
                ResKit.error(ctx,"channel is null");
                return;
            }
            if(r.temp == 1) {
                channel.removeAllTmpSubscriber();
            } else {
                store.removeSubscribers(r.channelId,r.channelType,r.subscribers);
                channel.removeSubscribers(r.subscribers);
                // TODO  :  current conversation need to be deleted
            }
            ResKit.success(ctx);
        };
    }
    @RouteMapping(path = "/block/add",method = HttpMethod.POST,block = true)
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
            CommonChannel channel = channelManager.getChannel(b.channelId, b.channelType);
            if(channel == null) {
                ResKit.error(ctx,"channel is null");
                return;
            }
            store.addDeniedList(b.channelId,b.channelType,b.uids);
            channel.addBlockList(b.uids);
            ResKit.success(ctx);
        };
    }
    @RouteMapping(path = "/block/set",method = HttpMethod.POST,block = true)
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
            CommonChannel channel = channelManager.getChannel(b.channelId, b.channelType);
            if(channel == null) {
                ResKit.error(ctx,"channel is null");
                return;
            }
            store.removeDeniedList(b.channelId,b.channelType,b.uids);
            store.addDeniedList(b.channelId,b.channelType,b.uids);
            channel.addBlockList(b.uids);
            ResKit.success(ctx);
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
            store.removeDeniedList(b.channelId,b.channelType,b.uids);
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
            CommonChannel channel = channelManager.getChannel(w.channelId, w.channelType);
            if(channel == null) {
                ResKit.error(ctx,"channel is null");
                return;
            }
            store.addAllowedList(w.channelId, w.channelType,w.uids);
            channel.addWhiteList(w.uids);
            ResKit.success(ctx);
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
            CommonChannel channel = channelManager.getChannel(w.channelId, w.channelType);
            if(channel == null) {
                ResKit.error(ctx,"channel is null");
                return;
            }
            store.removeAllowedList(w.channelId, w.channelType,w.uids);
            channel.removeWhiteList(w.uids);
            ResKit.success(ctx);
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
            CommonChannel channel = channelManager.getChannel(w.channelId, w.channelType);
            if(channel == null) {
                ResKit.error(ctx,"channel is null");
                return;
            }
            store.removeAllowedList(w.channelId, w.channelType,w.uids);
            store.addAllowedList(w.channelId, w.channelType,w.uids);
            channel.addWhiteList(w.uids);
            ResKit.success(ctx);
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
            CommonChannel channel = channelManager.getChannel(channelId, CS.ChannelType.Group);
            if(channel == null) {
                ResKit.error(ctx,"channel is null");
                return;
            }
            List<String> whiteList = channel.getWhiteList();
            if(whiteList == null || whiteList.isEmpty()) {
                ResKit.error(ctx,"whiteList is null");
                return;
            }
            JsonObject res = new JsonObject();
            for (String s : whiteList) {
                res.put(s,true);
            }
            ResKit.JSON(ctx,200,res);
        };
    }
    @RouteMapping(path = "/syncMessage",method = HttpMethod.POST)
    public Handler<RoutingContext> syncMessage() {
        return ctx -> {

        };
    }
}
