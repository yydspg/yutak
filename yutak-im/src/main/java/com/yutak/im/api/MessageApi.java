package com.yutak.im.api;

import com.yutak.vertx.anno.RouteHandler;
import com.yutak.vertx.anno.RouteMapping;
import com.yutak.vertx.core.HttpMethod;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

@RouteHandler("/message")
public class MessageApi {

    // send message
    @RouteMapping(path = "/send",method = HttpMethod.POST)
    public Handler<RoutingContext> send() {
        return ctx -> {

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

}
