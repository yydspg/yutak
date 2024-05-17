package com.yutak.server.mod.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yutak.orm.domain.Device;
import com.yutak.orm.service.DeviceService;
import com.yutak.orm.service.UserService;
import com.yutak.vertx.anno.RouteHandler;
import com.yutak.vertx.anno.RouteMapping;
import com.yutak.vertx.core.HttpMethod;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@RouteHandler("/user")
@Component
@RequiredArgsConstructor
public class UserApi {
//    private static final Logger log = LoggerFactory.getLogger(UserApi.class);

    private final UserService userService;
    private final DeviceService deviceService;

    @RouteMapping(path = "/device/delete", method = HttpMethod.GET)
    public Handler<RoutingContext> delDevice() {
        return ctx -> {
            LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Device::getDeviceId, ctx.request().getParam("deviceId"));
            wrapper.eq(Device::getUid, ctx.request().getParam("uid"));
            ctx.end("ok");
            ctx.vertx().executeBlocking(() -> userService.removeById(wrapper));
        };
    }
    @RouteMapping(path = "/device/list",method = HttpMethod.GET)
    public Handler<RoutingContext> deviceList() {
        return ctx -> {
            LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Device::getUid, ctx.request().getParam("uid"));
            wrapper.orderByAsc(Device::getLastLogin);
            ctx.vertx().executeBlocking(()->deviceService.list(wrapper)).onSuccess(t->{
                if( t == null ) {
                    ctx.end("no device found");
                    return;
                }
                JsonObject json = new JsonObject(t.stream().collect(Collectors.toMap(Device::getDeviceName, d -> d)));
                ctx.end(json.encode());
            });
        };
    }
}
