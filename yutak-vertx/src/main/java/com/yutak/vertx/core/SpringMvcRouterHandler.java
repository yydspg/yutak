package com.yutak.vertx.core;


import com.yutak.vertx.anno.*;
import com.yutak.vertx.kit.*;
import com.yutak.vertx.template.AbstractTemplateEngineDelegate;
import com.yutak.vertx.template.VertxTemplateEngine;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.VertxException;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

public class SpringMvcRouterHandler {
    private static final Logger log = LoggerFactory.getLogger(SpringMvcRouterHandler.class);
    private final  BeanFactory beanFactory;
    private final VertxHttpServerConfig httpServerConfig;
    private final List<IReqInterceptor> interceptorList = new ArrayList<>();

    public SpringMvcRouterHandler(VertxHttpServerConfig httpServerConfig) {
        this.beanFactory = httpServerConfig.getBeanFactory();
        this.httpServerConfig = httpServerConfig;
    }

    public VertxHttpServerConfig getHttpServerConfig() {
        return httpServerConfig;
    }

    public Router routerHandle() {
        // cors config
        Router router = httpServerConfig.getRouter();
        // config cors
        router.route().handler(CorsHandler.create()
                .allowedMethods(new HashSet<HttpMethod>() {{
                    add(HttpMethod.GET);
                    add(HttpMethod.POST);
                    add(HttpMethod.OPTIONS);
                    add(HttpMethod.PUT);
                    add(HttpMethod.DELETE);
                    add(HttpMethod.HEAD);
                }}));
        // request body handler --> json
        router.route().handler(BodyHandler.create(false));
        // init Interceptor
        initInterceptor(router);
        // init message Convertor
        initVertxMessageConverter();
        // init template engine
        initVertxTemplateEngine();
        // pre inception
        registerInterceptorPreHandler(router);
        // process route handler
        registerRouterHandler(router);
        // process Exception
        registerRouterExceptionHandler(router);
        // after inception
        registerInterceptorAfterHandler(router);

        return router;
    }
    private void registerInterceptorPreHandler(Router router) {
        interceptorList.forEach(interceptor -> {
            String[] path = interceptor.getClass().getAnnotation(Interceptor.class).value();
            if(path == null || path.length == 0) {
                doRegisterInterceptorPreHandler(router,null,interceptor);
            } else {
                Arrays.stream(path).forEach(subPath-> {
                    doRegisterInterceptorPreHandler(router,subPath,interceptor);
                });
            }
        });
    }
    // TODO  :  figure out this method
    private void doRegisterInterceptorPreHandler(Router router,String path,IReqInterceptor interceptor) {
        Route route = null;
        if(path == null || path.length() == 0){
            route = router.route();
        }else route = router.route(path);
        // register preHandler
        route.order(Integer.MIN_VALUE).handler(ctx->{
            HttpServerResponse response = ctx.response();
            if(response.ended()) return;
            if(response.ended()) return;
            if(!interceptor.pre(ctx)) {
                ResKit.End(response);
                return;
            }
            ctx.next();
        });
    }
    private void registerInterceptorAfterHandler(Router router) {
        List<IReqInterceptor> afterList = new ArrayList<>(interceptorList);
        Collections.reverse(afterList);
        afterList.forEach(interceptor -> {
            String[] path = interceptor.getClass().getAnnotation(Interceptor.class).value();
            if (path == null || path.length == 0) {
                doRegisterInterceptorAfterHandler(router, null, interceptor);
            } else {
                Arrays.stream(path).forEach(subPath -> {
                    doRegisterInterceptorAfterHandler(router, subPath, interceptor);
                });
            }

        });
    }
    private void doRegisterInterceptorAfterHandler(Router router, String path, IReqInterceptor interceptor) {
        try {
            Route route = null;
            if (Objects.isNull(path)) {
                route = router.route();
            } else {
                route = router.route(path);
            }
            route.last().handler(ctx -> {
                HttpServerResponse response = ctx.response();
                if (response.ended()) return;
                if (response.ended()) return;
                if (!interceptor.after(ctx)) {
                    ResKit.End(ctx.response());
                    return;
                }
                ctx.next();
            });
        } catch (Exception e) {
            log.error("regist after interceptorHandler error", e);
            throw new VertxException(e);
        }
    }
    private void registerRouterExceptionHandler(Router router) {
        Set<Object> exceptionHandlers = beanFactory.getTypesAnnotatedWith(RouterAdvice.class);
        if(exceptionHandlers == null || exceptionHandlers.size() == 0)  return;
        Map<Class<? extends Throwable>,Handler<RoutingContext>> exceptionHandlerMap = new HashMap<>();
        exceptionHandlers.forEach(routerAdviceHandler->{
            Set<Method> exceptionHandlerMethods = ReflectKit.getMethodsWithAnnotation(routerAdviceHandler.getClass(), ExceptionHandler.class);
            exceptionHandlerMethods.forEach(method->{
                ExceptionHandler anno = method.getAnnotation(ExceptionHandler.class);
                Handler<RoutingContext> errorHandler = RouteKit.resolveHandler(method, routerAdviceHandler);
                Class<? extends Throwable>[] value = anno.value();
                if(value != null && value.length > 0) {
                    Arrays.stream(value).forEach(error->{
                        exceptionHandlerMap.put(error, errorHandler);
                    });
                }
            });
        });
        if(!exceptionHandlerMap.isEmpty()) {
            // register failure handler
            // real code execute
            router.route().failureHandler(ctx->{
                Throwable failure = ctx.failure();
                // search for right exception handler
                Handler<RoutingContext> handler = exceptionHandlerMap.get(failure);
                if(handler != null) {
                  handler.handle(ctx);
                  return;
                }
                log.error("can not find error handler for:[{}]", failure.getMessage());
                JsonObject error = new JsonObject();
                error.put("message", failure.getMessage());
                ResKit.JSON(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), error);
            });
        }
    }
    // register RouterHandler
    private void registerRouterHandler(Router router) {
        Set<Object> routeHandlers = beanFactory.getTypesAnnotatedWith(RouteHandler.class);
        if(routeHandlers == null || routeHandlers.size() == 0) return;
        routeHandlers.forEach(handler->{
            // extract current RouteHandler route info
            List<RouteInfo> routeInfos = extractRouteInfos(handler.getClass());
            routeInfos.stream().sorted(Comparator.comparingInt(RouteInfo::getOrder)).forEach(routeInfo->{
                // binding method handler
                Handler<RoutingContext> methodHandler = routeInfo.getMethodHandler();
                Route route = router.route(routeInfo.getRouteMethod(), routeInfo.getRoutePath());
                // set blocking handler ,such as sql execute handler
                if(routeInfo.isBlocked()) route.blockingHandler(methodHandler);
                else route.handler(methodHandler);
                String[] consumes = routeInfo.getConsumes();
                if (StringKit.isNotEmpty(List.of(consumes))) {
                    Arrays.stream(consumes).forEach(route::consumes);
                }
                String[] produces = routeInfo.getProduces();
                if(StringKit.isNotEmpty(List.of(produces))) {
                    Arrays.stream(produces).forEach(route::produces);
                }
            });
        });
    }
    private List<RouteInfo> extractRouteInfos(Class routeHandlerClass) {
        RouteHandler routeHandlerAnnotation = RouteKit.getRouteHandler(routeHandlerClass);
        String serverPrefix = httpServerConfig.getServerBasePath();
        String routePrefix = routeHandlerAnnotation.value();
        Object o = beanFactory.get(routeHandlerClass);
        return Stream.of(routeHandlerClass.getMethods()).filter(method -> method.isAnnotationPresent(RouteMapping.class))
                .map(method -> {
                    RouteMapping routeMapping = RouteKit.getRouteMapping(method);
                    com.yutak.vertx.core.HttpMethod httpMethod = routeMapping.method();
                    String routeUrl = RouteKit.buildRouteUrl(serverPrefix, routePrefix, routeMapping.path());
                    RouteInfo r = new RouteInfo();
                    r.setRoutePath(routeUrl);
                    //set route method
                    r.setRouteMethod(HttpMethod.valueOf(httpMethod.name()));
                    r.setOrder(routeMapping.order());
                    r.setConsumes(routeMapping.consumes());
                    r.setProduces(routeMapping.produces());
                    RouteKit.resolveRouteMethodHandler(method, o, httpServerConfig, r);
                    return r;
                }).toList();
    }
    private void initVertxTemplateEngine() {
        httpServerConfig.setTemplateEngineMap(new LinkedHashMap<>());
        Set<Object> templates = beanFactory.getTypesAnnotatedWith(VertxTemplateEngine.class);
        if (templates == null || templates.isEmpty())  return ;
        Map<String, AbstractTemplateEngineDelegate> templateEngineMap = httpServerConfig.getTemplateEngineMap();
        templates.stream()
                .sorted(Comparator.comparing(o -> OrderKit.getOrder(o,0)))
                .forEach(o -> {
                    VertxTemplateEngine annotation = (VertxTemplateEngine)  o.getClass().getAnnotation(VertxTemplateEngine.class);
                    AbstractTemplateEngineDelegate engine = (AbstractTemplateEngineDelegate) o;
                    engine.setVertx(httpServerConfig.getVertx());
                    engine.setBasePath(annotation.basePath());
                    engine.after();
                    templateEngineMap.put(annotation.name(), engine);
                });
    }
    private void initInterceptor(Router router) {
        Set<Object> interceptors = beanFactory.getTypesAnnotatedWith(Interceptor.class);
        if(interceptors.isEmpty()) return ;
        interceptors.stream().filter(o -> {
            Class<?> clz = o.getClass();
            return clz.getAnnotation(Interceptor.class) != null && ReflectKit.isClassImplement(clz,IReqInterceptor.class);
        }).sorted(Comparator.comparingInt(t-> OrderKit.getOrder(t,VertxCS.INTERCEPTOR_PRE_DEFAULT_ORDER)))
                .forEach(interceptor -> {
                    IReqInterceptor t = (IReqInterceptor) interceptor;
                    log.info("register interceptor {} ", t);
                    interceptorList.add(t);
                });
    }
    private void initVertxMessageConverter() {
        httpServerConfig.setMessageConvertorList(new ArrayList<>());
        Set<Object> messageConverters = beanFactory.getTypesAnnotatedWith(MessageConvertor.class);
        if(messageConverters.isEmpty()) return;

        List<MessageConvertor> messageConverterList = httpServerConfig.getMessageConvertorList();
        messageConverterList.stream()
                .sorted(Comparator.comparingInt(t->OrderKit.getOrder(t,0)))
                .forEach(messageConverterList::add);
    }
    public void registerMessageConverter(MessageConvertor t) {
        httpServerConfig.getMessageConvertorList().add(t);
    }
    public void registerMessageConvertor(int order, MessageConvertor t) {
        httpServerConfig.getMessageConvertorList().add(order,t);
    }
}
