package com.yutak.vertx.core;


import com.yutak.vertx.template.AbstractTemplateEngineDelegate;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import lombok.Data;

import java.net.ServerSocket;
import java.util.List;
import java.util.Map;

@Data
public class VertxHttpServerConfig {
    /**
     * http端口
     */
    public  Integer httpPort;
    /**
     * 工作线程池大小
     */
    public Integer workPoolSize;
    /**
     * event 执行超时时间
     */
    public  Integer eventBusconnectTimeout;
    /*
    core concept
     */
    public Vertx vertx;
    /*
    vertx-web
     */
    public Router router;
    /**
     * 静态资源目录 多个,分隔
     */
    public String staticDir;
    /**
     * split
     */
    public  String basePackages;
    /**
     * bean 创建工厂
     */
    public  BeanFactory beanFactory;

    /**
     * 模板引擎
     */
    public Map<String, AbstractTemplateEngineDelegate> templateEngineMap;
    /**
     * 模板基础目录
     */
    public  String templateDir;
    /**
     * 消息转换器
     */
    public  List<MessageConvertor> messageConvertorList;

    /**
     * 服务基础路径
     */
    public String serverBasePath;

    /*
        web socket server
     */
    public Handler<ServerWebSocket> serverSocketHandler;
}
