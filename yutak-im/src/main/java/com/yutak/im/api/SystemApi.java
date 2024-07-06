package com.yutak.im.api;

import com.yutak.im.core.YutakNetServer;
import com.yutak.im.store.YutakStore;
import com.yutak.vertx.anno.RouteHandler;
import com.yutak.vertx.anno.RouteMapping;
import com.yutak.vertx.core.HttpMethod;
import com.yutak.vertx.kit.ReqKit;
import com.yutak.vertx.kit.ResKit;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

@RouteHandler("/system")
public class SystemApi {

    private final YutakStore yutakStore;
    public SystemApi() {

        yutakStore = YutakStore.get();
    }
    @RouteMapping(path = "/block/add",method = HttpMethod.POST)
    public Handler<RoutingContext> addBlock() {
        return ctx -> {
            JsonObject json = ReqKit.getJSON(ctx);
            if (json == null) {
                ResKit.error(ctx,"no invalid json");
                return;
            }
            JsonArray ips = json.getJsonArray("ips");
            if (ips == null) {
                ResKit.error(ctx, "ip list is empty");
                return;
            }
            List<String> list = ips.getList();
            YutakNetServer.get().addBlockIp(list);
            ResKit.success(ctx);
        };
    }
    @RouteMapping(path = "/block/remove",method = HttpMethod.POST)
    public Handler<RoutingContext> removeBlock() {
        return ctx -> {
            JsonObject json = ReqKit.getJSON(ctx);
            if (json == null) {
                ResKit.error(ctx,"no invalid json");
                return;
            }
            JsonArray ips = json.getJsonArray("ips");
            if (ips == null) {
                ResKit.error(ctx, "ip list is empty");
                return;
            }
            List<String> list = ips.getList();
            YutakNetServer.get().removeBlockIp(list);
            ResKit.success(ctx);
        };
    }
    // TODO  :  need load block list ip when server start right now
    @RouteMapping(path = "/block/list",method = HttpMethod.GET)
    public Handler<RoutingContext> listBlock() {
        return ctx -> {
            ResKit.success(ctx,YutakNetServer.get().IPBlockList.keySet());
        };
    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());

    }
}
