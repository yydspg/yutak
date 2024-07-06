package com.yutak.im.api;

import com.yutak.im.core.ConnectManager;
import com.yutak.im.domain.Conn;
import com.yutak.im.domain.Req;
import com.yutak.im.domain.Res;
import com.yutak.im.proto.CS;
import com.yutak.vertx.anno.RouteHandler;
import com.yutak.vertx.anno.RouteMapping;
import com.yutak.vertx.core.HttpMethod;
import com.yutak.vertx.kit.ReqKit;
import com.yutak.vertx.kit.ResKit;
import com.yutak.vertx.kit.StringKit;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RouteHandler("connz")

public class ConnzApi {
    private final ConnectManager connectManager;
    public ConnzApi() {
        connectManager = ConnectManager.get();
    }
    @RouteMapping(path = "/conn",method = HttpMethod.POST)
    public Handler<RoutingContext> getConnByUid() {
        return ctx -> {
            JsonObject json = ReqKit.getJSON(ctx);
//            int offset = Integer.parseInt(json.getString("offset"));
//            int limit = Integer.parseInt( json.getString("limit"));
            String uid = json.getString("uid");
            if(StringKit.isEmpty(uid)) {
                ResKit.error(ctx,"no invalid uid");
                return;
            }
            List<Conn> conns = connectManager.getConnect(uid);
            if(conns == null || conns.isEmpty()) {
                ResKit.error(ctx,"no such conn");
                return;
            }
            List<Res.Conn> resConns = new ArrayList<>();
            for(Conn conn : conns) {
                resConns.add(buildResConn(conn));
            }
            ResKit.success(ctx,resConns);
        };
    }
    private Res.Conn buildResConn(Conn c) {
        Res.Conn r = new Res.Conn();
        String[] args = c.remoteAddr.split(":");
//        r.offset = offset;
//        r.limit = limit;
        r.connInfo = new Res.ConnInfo();
        r.connInfo.ID = c.id;
        r.connInfo.UID = c.uid;
        r.connInfo.IP = args[0];
        r.connInfo.lastActivity = c.lastActivity;
        r.connInfo.inboundMsgs = c.inMsgs.get();
        r.connInfo.outboundMsgs = c.outMsgs.get();
        r.connInfo.device = buildDeviceInfo(c.deviceLevel,c.deviceFlag);
        r.connInfo.deviceID = c.deviceID;
        r.connInfo.port = Integer.parseInt(args[1]);
        return r;
    }
    private String buildDeviceInfo(byte level,byte flag) {
        String args1 = switch (flag){
            case CS.Device.Flag.web -> "Web";
            case CS.Device.Flag.app -> "app";
            case CS.Device.Flag.pc ->  "pc";
            default -> "unknown";
        };
        String args2 = switch (level) {
            case CS.Device.Level.master -> "master";
            case CS.Device.Level.slave -> "slave";
            default -> "unknown";
        };
        return args1 +"(" +args2+")";
    }
}
