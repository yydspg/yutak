package com.yutak.im.api;

import com.yutak.vertx.anno.RouteHandler;
import com.yutak.vertx.anno.RouteMapping;
import com.yutak.vertx.core.HttpMethod;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

@RouteHandler("/system")
public class SystemApi {
    @RouteMapping(path = "/block/add",method = HttpMethod.POST)
    public Handler<RoutingContext> addBlock() {
        return ctx -> {

        };
    }
    @RouteMapping(path = "/block/remove",method = HttpMethod.POST)
    public Handler<RoutingContext> removeBlock() {
        return ctx -> {

        };
    }
    @RouteMapping(path = "/block/list",method = HttpMethod.GET)
    public Handler<RoutingContext> listBlock() {
        return ctx -> {

        };
    }
}
