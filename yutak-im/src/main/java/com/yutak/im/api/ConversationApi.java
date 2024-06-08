package com.yutak.im.api;

import com.yutak.vertx.anno.RouteHandler;
import com.yutak.vertx.anno.RouteMapping;
import com.yutak.vertx.core.HttpMethod;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;


@RouteHandler("/conversation")
public class ConversationApi {
    // stream send msg end
    @RouteMapping(path = "/list",method = HttpMethod.GET)
    public Handler<RoutingContext> list() {
        return ctx->{

        };
    }
    // clear conversation number
    @RouteMapping(path = "/list",method = HttpMethod.POST)
    public Handler<RoutingContext> clearUnread() {
        return ctx->{

        };
    }
    // set conversation number
    @RouteMapping(path = "/setUnread",method = HttpMethod.POST)
    public Handler<RoutingContext> setUnread() {
        return ctx->{

        };
    }
    // delete conversation
    @RouteMapping(path = "/delete",method = HttpMethod.POST)
    public Handler<RoutingContext> delete() {
        return ctx->{

        };
    }
    // sync conversation
    @RouteMapping(path = "/sync",method = HttpMethod.POST)
    public Handler<RoutingContext> sync() {
        return ctx->{

        };
    }
    // sync recent message
    @RouteMapping(path = "/sync Message",method = HttpMethod.POST)
    public Handler<RoutingContext> syncMessage() {
        return ctx->{

        };
    }
}
