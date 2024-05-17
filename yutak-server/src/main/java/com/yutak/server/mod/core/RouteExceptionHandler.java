package com.yutak.server.mod.core;

import com.yutak.vertx.anno.ExceptionHandler;
import com.yutak.vertx.anno.RouterAdvice;
import com.yutak.vertx.kit.ResKit;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;

@Component
@RouterAdvice
public class RouteExceptionHandler {
    @ExceptionHandler(value = YutakException.class)
    public Handler<RoutingContext> handle() {
        return ctx -> {
            Throwable failure = ctx.failure();
            ResKit.JSON(ctx,500,failure.getMessage());
        };
    }
}

