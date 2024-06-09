package com.yutak.im.api;

import com.yutak.im.core.ConnectManager;
import com.yutak.im.core.DeliveryManager;
import com.yutak.im.domain.Conn;
import com.yutak.im.domain.Req;
import com.yutak.im.proto.CS;
import com.yutak.im.proto.DisConnectPacket;
import com.yutak.im.store.H2Store;
import com.yutak.im.store.Store;
import com.yutak.vertx.anno.RouteHandler;
import com.yutak.vertx.anno.RouteMapping;
import com.yutak.vertx.core.HttpMethod;
import com.yutak.vertx.kit.ReqKit;
import com.yutak.vertx.kit.ResKit;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

@RouteHandler("/user")
public class UserApi {
    private Store store;
    private final ConnectManager connectManager;
    private final DeliveryManager deliveryManager;
    public UserApi() {
        store = H2Store.get();
        connectManager = ConnectManager.get();
        deliveryManager = DeliveryManager.get();
    }
    // update user token
    @RouteMapping(path = "/token",method = HttpMethod.POST)
    public Handler<RoutingContext> updateToken() {
        return ctx -> {
            Req.UpdateToken u = ReqKit.getObjectInBody(ctx, Req.UpdateToken.class);

            if(u == null) {
                ResKit.error(ctx,"no update token");
                return;
            }

        };
    }
    @RouteMapping(path = "/deviceQuit",method = HttpMethod.POST)
    public Handler<RoutingContext> deviceQuit() {
        return ctx -> {
            Req.DeviceQuit r = ReqKit.getObjectInBody(ctx, Req.DeviceQuit.class);
            if(r == null) {
                ResKit.error(ctx,"no invalid request");
                return;
            }
            if(r.deviceFlag == CS.Device.Flag.all) {
                quitUserService(r.uid, CS.Device.Flag.app);
                quitUserService(r.uid, CS.Device.Flag.pc);
                quitUserService(r.uid, CS.Device.Flag.web);
            } else {
                quitUserService(r.uid,r.deviceFlag);
            }
            ResKit.success(ctx);
        };
    }
    @RouteMapping(path = "/onlineStatus",method = HttpMethod.POST)
    public Handler<RoutingContext> onlineStatus() {
        return ctx -> {

        };
    }
    @RouteMapping(path = "/addSystemUid",method = HttpMethod.POST)
    public Handler<RoutingContext> addSystemUid() {
        return ctx -> {

        };
    }
    @RouteMapping(path = "/delSystemUid",method = HttpMethod.POST)
    public Handler<RoutingContext> removeSystemUid() {
        return ctx -> {

        };
    }

    private void quitUserService(String uid, byte deviceFlag) {
        // update user token
        store.updateUserToken(uid,"",deviceFlag, CS.Device.Level.master);
        List<Conn> connects = connectManager.getConnectWithDeviceFlag(uid, deviceFlag);
        if(connects != null) {
            for(Conn conn : connects) {
                // send disconnect packet
                DisConnectPacket d = new DisConnectPacket();
                d.reasonCode = CS.ReasonCode.ConnectKick;
                deliveryManager.dataOut(conn,List.of(d));
                // close current conn
                connectManager.removeConnect(conn.id);
            }
        }
    }
}
