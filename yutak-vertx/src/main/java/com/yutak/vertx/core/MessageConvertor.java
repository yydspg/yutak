package com.yutak.vertx.core;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface MessageConvertor {
    boolean support(Object source);

    Handler<RoutingContext> convertTo(Object source);

}
