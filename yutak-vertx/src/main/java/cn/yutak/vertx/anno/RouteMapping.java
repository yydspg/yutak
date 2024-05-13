package cn.yutak.vertx.anno;

import cn.yutak.vertx.core.VertxCS;
import io.vertx.core.http.HttpMethod;

import java.lang.annotation.*;


@Target(ElementType.METHOD )
@Retention(RetentionPolicy.RUNTIME)
public @interface RouteMapping {
    String path() default "";
    // damn ! HttpMethod in Vertx has already changed ,so use this Class<? extends HttpMethod> to replace
    Class<? extends HttpMethod> method() default HttpMethod.class;
    boolean block() default false;
    String[] consumes() default {};
    String[] produces() default {};
    String desc() default "";
    int order() default VertxCS.AUTO_REGIST_ROUTE_ORDER;
}
