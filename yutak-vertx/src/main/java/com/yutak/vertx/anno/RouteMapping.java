package com.yutak.vertx.anno;

import com.yutak.vertx.core.HttpMethod;
import com.yutak.vertx.core.VertxCS;

import java.lang.annotation.*;


@Target(ElementType.METHOD )
@Retention(RetentionPolicy.RUNTIME)
public @interface RouteMapping {
    String path() default "";
    // damn ! HttpMethod in Vertx has already changed ,so use this Class<? extends HttpMethod> to replace
    HttpMethod method() default HttpMethod.GET;
    boolean block() default false;
    String[] consumes() default {};
    String[] produces() default {};
    String desc() default "";
    int order() default VertxCS.AUTO_REGIST_ROUTE_ORDER;
}
