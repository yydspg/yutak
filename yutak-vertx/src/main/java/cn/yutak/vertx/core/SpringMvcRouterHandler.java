package cn.yutak.vertx.core;

import cn.yutak.vertx.anno.Interceptor;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        router.route().handler(CorsHandler.create()
                .allowedMethods(new HashSet<>() {{
                    add(HttpMethod.GET);
                    add(HttpMethod.POST);
                    add(HttpMethod.OPTIONS);
                    add(HttpMethod.PUT);
                    add(HttpMethod.DELETE);
                    add(HttpMethod.HEAD);
                }}));
        router.route().handler(BodyHandler.create(true));
        // init Interceptor
        return null;
    }

    private void initInterceptor(Router router) {
        Set<Object> interceptors = beanFactory.getTypesAnnotatedWith(Interceptor.class);
        if(interceptors.isEmpty()) return ;

    }
}
