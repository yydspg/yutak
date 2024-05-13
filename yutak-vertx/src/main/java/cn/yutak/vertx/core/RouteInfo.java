package cn.yutak.vertx.core;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class RouteInfo {
    private String routePath;
    private HttpMethod routeMethod;
    private String[] consumes;
    private String[] produces;
    private Integer order;
//    public static List<HttpMethod> allRouteMethod = null;
    private boolean blocked;
    private Handler<RoutingContext> methodHandler;

}
