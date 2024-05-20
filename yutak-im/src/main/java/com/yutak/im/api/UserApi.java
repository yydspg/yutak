package com.yutak.im.api;

import com.yutak.vertx.anno.RouteHandler;
import com.yutak.vertx.anno.RouteMapping;
import com.yutak.vertx.core.HttpMethod;
import com.yutak.vertx.kit.ResKit;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

@RouteHandler("/user")
public class UserApi {
    @RouteMapping(path = "/token",method = HttpMethod.POST)
    public Handler<RoutingContext> updateToken() {
        return ctx -> {

        };
    }@RouteMapping(path = "/deviceQuit",method = HttpMethod.POST)
    public Handler<RoutingContext> deviceQuit() {
        return ctx -> {

        };
    }
    @RouteMapping(path = "/onlineStatus",method = HttpMethod.POST)
    public Handler<RoutingContext> onlineStatus() {
        return ctx -> {

        };
    }
    @RouteMapping(path = "/addSystemUid",method = HttpMethod.POST)
    public Handler<RoutingContext> addSystemUid() {
        return ctx -> {

        };
    }
    @RouteMapping(path = "/delSystemUid",method = HttpMethod.POST)
    public Handler<RoutingContext> removeSystemUid() {
        return ctx -> {

        };
    }


}
