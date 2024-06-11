package com.yutak.vertx.kit;

import io.vertx.core.Future;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
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
    public static void buildResWithFuture(Future<?> future,RoutingContext ctx) {
//        future.onFailure(t->{
//            ctx.response().setStatusCode(500);
//            ctx.response().end(t.getMessage());
//        }).onSuccess(res->{
//            ctx.response().setStatusCode(200);
//            Future.succeededFuture();
//        });
    }
    public static void error(RoutingContext ctx,String msg) {
        JsonObject res = new JsonObject();
        res.put("error", msg);
        ctx.response().setStatusCode(500).end(res.toString());
    }
    public static void success(RoutingContext ctx,Object o) {
        String encode = Json.encode(o);
        ctx.response().setStatusCode(200).end(encode);
    }
    public static void success(RoutingContext ctx) {
        ctx.response().setStatusCode(200).end("ok");
    }
}
