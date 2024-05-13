package cn.yutak.vertx.kit;

import cn.yutak.vertx.anno.ResBody;
import cn.yutak.vertx.anno.RouteHandler;
import cn.yutak.vertx.anno.RouteMapping;
import cn.yutak.vertx.core.HandlerWrapper;
import cn.yutak.vertx.core.RouteInfo;
import cn.yutak.vertx.core.VertxHttpServerConfig;
import cn.yutak.vertx.template.TemplateBody;
import io.vertx.core.Handler;
import io.vertx.core.VertxException;
import io.vertx.ext.web.RoutingContext;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class RouteKit {
    public static RouteMapping getRouteMapping(Method routeHandlerMethod) {
        if(routeHandlerMethod.isAnnotationPresent(RouteMapping.class)) {
            return routeHandlerMethod.getAnnotation(RouteMapping.class);
        }
        return null;
    }
    public static RouteHandler getRouteHandler(Class<?> routeHandlerClass) {
        if(routeHandlerClass.isAnnotationPresent(RouteHandler.class)) {
            return routeHandlerClass.getAnnotation(RouteHandler.class);
        }
        return null;
    }
    public static String buildRouteUrl(String prefix,String ... path) {
        StringBuilder routeUrl = new StringBuilder();
        if(prefix != null ) routeUrl.append("/").append(routeSubPath(prefix));
        Arrays.stream(path).filter(Objects::nonNull).forEach(subPath -> {
            routeUrl.append("/");
            routeUrl.append(routeSubPath(subPath));
        });
        return routeUrl.toString();
    }
    public static String routeSubPath(String path) {
        if (path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
    // TODO  :  focus this method ,try to figure out its role
    public static void resolveRouteMethodHandler(Method method, Object instance, VertxHttpServerConfig vHttpServerConfig, RouteInfo routeInfo) {
        try {
            // check method is blocked
            routeInfo.setBlocked(judgeMethodBlock(method));

            if(method.isAnnotationPresent(ResBody.class) || method.getDeclaringClass().isAnnotationPresent(ResBody.class)) {
                routeInfo.setMethodHandler(HandlerWrapper.JSONWrapper(vHttpServerConfig,method,instance));
                return ;
            }
            TemplateBody routeAnnotation = (TemplateBody) getAnnotation(method, TemplateBody.class);
            if(routeAnnotation != null) {
                routeInfo.setMethodHandler(HandlerWrapper.templateWrapper(vHttpServerConfig,method,instance,routeAnnotation));
                return;
            }
            Object res = method.invoke(instance);
            if(res instanceof Handler) {
                routeInfo.setMethodHandler((Handler<RoutingContext>) res);
                return;
            }
        }catch (Exception e) {
            throw new VertxException(e);
        }
        throw new VertxException("route handler not support");
    }
    public static boolean judgeMethodBlock(Method method) {
        /*
        logic of judgement:
            1. if handlerClass has @interface @ResponseBody or @TemplateBody // must be blocked
            2. else according properties [block()]
         */
        // check class
        if (method.getDeclaringClass().isAnnotationPresent(ResBody.class) || method.getDeclaringClass().isAnnotationPresent(TemplateBody.class)) return true;
        // check method
        else return method.isAnnotationPresent(ResBody.class) || method.isAnnotationPresent(TemplateBody.class) || method.getAnnotation(RouteMapping.class).block();
    }
    public static Annotation getAnnotation(Method method,Class annotationClass) {
        Annotation annotation = method.getAnnotation(annotationClass);
        if (annotation == null) {
            return method.getDeclaringClass().getAnnotation(annotationClass);
        }
        return annotation;
    }
    public static Handler<RoutingContext> resolveHandler(Method method,Object instance) {
        try {
            return (Handler<RoutingContext>) method.invoke(instance);
        } catch (Exception e) {
            throw new VertxException("route handler error",e);
        }
    }
}
