package com.yutak.im.api;

import com.yutak.im.core.ChannelManager;
import com.yutak.im.core.ConnectManager;
import com.yutak.im.core.DeliveryManager;
import com.yutak.im.core.SystemUIDManager;
import com.yutak.im.domain.CommonChannel;
import com.yutak.im.domain.Conn;
import com.yutak.im.domain.Req;
import com.yutak.im.domain.Res;
import com.yutak.im.proto.CS;
import com.yutak.im.proto.DisConnectPacket;
import com.yutak.im.store.H2Store;
import com.yutak.im.store.Store;
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

import java.util.ArrayList;
import java.util.List;

@RouteHandler("/user")
public class UserApi {
    private final ConnectManager connectManager;
    private final DeliveryManager deliveryManager;
    private final ChannelManager channelManager;
    private final SystemUIDManager systemUIDManager;
    private final YutakStore yutakStore;
    public UserApi() {

        connectManager = ConnectManager.get();
        deliveryManager = DeliveryManager.get();
        channelManager = ChannelManager.get();
        systemUIDManager  = SystemUIDManager.get();
        yutakStore = YutakStore.get();
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
            // create or update channel info in memory
            CommonChannel channel = channelManager.getChannel(u.uid, CS.ChannelType.Person);
            if(channel == null) {
                ResKit.error(ctx,"no channel");
                return;
            }
            yutakStore.updateUserToken(u.uid,u.deviceFlag,u.deviceLevel, u.token);

            // remove old connections
            if (u.deviceLevel == CS.Device.Level.master) {
                List<Conn> conns = connectManager.getConnectWithDeviceFlag(u.uid, u.deviceFlag);
                for (Conn conn : conns) {
                    DisConnectPacket d = new DisConnectPacket();
                    d.reasonCode = CS.ReasonCode.ConnectKick;
                    d.reason = "login on other device";
                    deliveryManager.dataOut(conn,List.of(d));
                    connectManager.removeConnect(conn.id);
                }
            }
            ResKit.success(ctx);
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
    @RouteMapping(path = "/onlineStatus",method = HttpMethod.POST,block = false)
    public Handler<RoutingContext> onlineStatus() {
        return ctx -> {
            long l = System.currentTimeMillis();
            JsonObject json = ReqKit.getJSON(ctx);
            if(json == null) {
                ResKit.error(ctx,"no invalid request");
                return;
            }
            JsonArray uids = json.getJsonArray("uids");
            if(uids == null) {
                ResKit.error(ctx,"no invalid uid");
                return;
            }
            List<Conn> conns = connectManager.getOnlineConn(uids.getList());
            ArrayList<Res.OnlineConnect> res = new ArrayList<>();
            conns.forEach(conn -> {
                Res.OnlineConnect connect = new Res.OnlineConnect();
                connect.uid = conn.uid;
                connect.deviceFlag = conn.deviceFlag;
                res.add(connect);
            });
            ResKit.success(ctx, res);
        };
    }
    @RouteMapping(path = "/addSystemUid",method = HttpMethod.POST)
    public Handler<RoutingContext> addSystemUid() {
        return ctx -> {
            JsonObject json = ReqKit.getJSON(ctx);
            if(json == null) {
                ResKit.error(ctx,"no invalid request");
                return;
            }
            JsonArray uids = json.getJsonArray("uids");
            if(uids == null) {
                ResKit.error(ctx,"no invalid uid");
                return;
            }
            if(uids.size() > 0) {
                systemUIDManager.addSystemUIDs(uids.getList());
            }
            ResKit.success(ctx);
        };
    }
    @RouteMapping(path = "/delSystemUid",method = HttpMethod.POST)
    public Handler<RoutingContext> removeSystemUid() {
        return ctx -> {
            JsonObject json = ReqKit.getJSON(ctx);
            if(json == null) {
                ResKit.error(ctx,"no invalid request");
                return;
            }
            JsonArray uids = json.getJsonArray("uids");
            if(uids == null || uids.size() == 0) {
                ResKit.error(ctx,"no invalid uid");
                return;
            }
            systemUIDManager.removeSystemUIDs(uids.getList());
            ResKit.success(ctx);
        };
    }

    private void quitUserService(String uid, byte deviceFlag) {
        // update user token
        yutakStore.updateUserToken(uid,deviceFlag, CS.Device.Level.master,"");
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
