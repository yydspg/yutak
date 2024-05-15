package com.yutak.vertx.kit;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class ResKit {

    public static void JSON(RoutingContext rc,int httpCode,String res) {
        rc.response()
                .setStatusCode(httpCode)
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .end(res);
    }
    public static void End(HttpServerResponse response) {
        if (response.ended()) {
            return;
        }
        response.end();
    }
    public static void JSON(RoutingContext routingContext, int httpcode, JsonObject result) {
        JSON(routingContext, httpcode, result.encode());
    }
}
