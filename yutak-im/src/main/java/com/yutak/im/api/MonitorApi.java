package com.yutak.im.api;

import com.yutak.im.core.ChannelManager;
import com.yutak.im.core.ConnectManager;
import com.yutak.im.core.YutakNetServer;
import com.yutak.im.domain.Res;
import com.yutak.vertx.anno.RouteHandler;
import com.yutak.vertx.anno.RouteMapping;
import com.yutak.vertx.core.HttpMethod;
import com.yutak.vertx.kit.ResKit;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

@RouteHandler("/monitor")
public class MonitorApi {

    private final ConnectManager connectManager;
    private final ChannelManager channelManager;
    public MonitorApi() {
        connectManager = ConnectManager.get();
        channelManager = ChannelManager.get();
    }
    @RouteMapping(path = "/conn",method = HttpMethod.GET)
    public Handler<RoutingContext> getConnNum() {
        return ctx -> {
            ResKit.success(ctx,connectManager.getOnlineCount());
        };
    }
    @RouteMapping(path = "/channel",method = HttpMethod.GET)
    public Handler<RoutingContext> getChannelNum() {
        return ctx -> {
            Res.ChannelNum res = new Res.ChannelNum();
            res.commonChannelNum = channelManager.getChannelNum();
            res.dataChannelNum = channelManager.getDataChannelNum();
            res.personChannelNum = channelManager.getPersonChannelNum();
            res.tmpChannelNum = channelManager.getTmpChannelNum();
            ResKit.success(ctx,res);
        };
    }
    @RouteMapping(path = "/message",method = HttpMethod.GET)
    public Handler<RoutingContext> message() {
        return ctx -> {
            JsonObject res = new JsonObject();
            res.put("InboundMessagesNum",YutakNetServer.get().status.inboundMessages.get());
            res.put("OutboundMessagesNum",YutakNetServer.get().status.outboundMessages.get());
            ResKit.JSON(ctx,200,res);
        };
    }
}

