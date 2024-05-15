package com.yutak.server.test;

import com.yutak.vertx.anno.RouteHandler;
import com.yutak.vertx.anno.RouteMapping;
import com.yutak.vertx.core.HttpMethod;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@RouteHandler("/test")
@Component
public class Demo {
    private static final Logger log = LoggerFactory.getLogger(Demo.class);

    @RouteMapping(path = "/api",method = HttpMethod.GET)
    public Handler<RoutingContext> route() {
        return ctx->{
            log.info("hello world!!");
            ctx.end();
        };
    }
}
