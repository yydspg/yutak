package com.yutak.vertx.core;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class JSONMessageConvertor implements MessageConvertor {


    @Override
    public boolean support(Object source) {
        return true;
    }

    @Override
    public Handler<RoutingContext> convertTo(Object source) {
        return null;
    }
}
