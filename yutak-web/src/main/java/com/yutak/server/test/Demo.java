package com.yutak.server.test;

import com.yutak.vertx.anno.RouteHandler;
import com.yutak.vertx.anno.RouteMapping;
import com.yutak.vertx.core.HttpMethod;
import com.yutak.vertx.kit.ReqKit;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@RouteHandler("/test")
@Component
public class Demo {
    private static final Logger log = LoggerFactory.getLogger(Demo.class);

    @RouteMapping(path = "/api",method = HttpMethod.POST,order = 1)
    public Handler<RoutingContext> route() {
        return ctx->{
            String name = ctx.request().getParam("name");
//            log.info(name);
//            log.info(ReqKit.getStrInPath(ctx.request(),"name"));
            log.info("第一级路由");
            log.info("主线程{}",Thread.currentThread().getName());
           ctx.next();
        };
    }
    @RouteMapping(path = "/api",method = HttpMethod.POST,order = 2)
    public Handler<RoutingContext> route2() {
        return ctx->{
            log.info(Thread.currentThread().getName());
            log.info("2级路由");
            ctx.vertx().executeBlocking(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    log.info("阻塞开始");
                    TimeUnit.SECONDS.sleep(5);
                    log.info("现在是vertx executing Blocking");
                    log.info("工作线程{}",Thread.currentThread().getName());
                    return null;
                }
            }).onComplete(new Handler<AsyncResult<Object>>() {
                @Override
                public void handle(AsyncResult<Object> event) {
                    log.info("工作线程{}",Thread.currentThread().getName());
                    log.info("现在回调完成了");
                    ctx.next();
                }
            });
            log.info("主线程没阻塞");
//            ctx.currentRoute().blockingHandler(event -> {
//                try {
//                    TimeUnit.SECONDS.sleep(5);
//                    log.info("现在是 blocking handler");
//                    log.info(Thread.currentThread().getName());
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//                ctx.response().end("ok");
//            });
        };
    }
    @RouteMapping(path = "/stop",method = HttpMethod.GET)
    public Handler<RoutingContext> route3() {
        return ctx->{
            log.info(Thread.currentThread().getName());
            return ;
        };
    }
    public static void main(String[] args) {
        String number = "89";
        System.out.println(Integer.parseInt(number));
    }
}
