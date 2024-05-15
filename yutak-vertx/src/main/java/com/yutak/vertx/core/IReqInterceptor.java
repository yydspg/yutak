package com.yutak.vertx.core;

import io.vertx.ext.web.RoutingContext;

public interface IReqInterceptor {
    /**
     * return 是否继续执行
     */
    boolean pre(RoutingContext routingContext);

    /**
     * return 是否继续执行
     */
    boolean after(RoutingContext routingContext);

}
