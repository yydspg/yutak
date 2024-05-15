package com.yutak.vertx.core;

import com.yutak.vertx.kit.ResKit;
import com.yutak.vertx.kit.StringKit;
import com.yutak.vertx.kit.TemplateKit;
import com.yutak.vertx.template.AbstractTemplateEngineDelegate;
import com.yutak.vertx.template.TemplateBody;
import io.vertx.core.Handler;
import io.vertx.core.VertxException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class HandlerWrapper {

    // JSON wrapper provider
    public static Handler<RoutingContext> JSONWrapper(VertxHttpServerConfig serverConfig, Method method, Object instance) {
        return ctx->{
            List<MessageConvertor> messageConvertorList = serverConfig.getMessageConvertorList();
            // maybe useless
            if (StringKit.isEmpty(messageConvertorList)) {
                throw new VertxException("messageConvertorList is empty");
            }

            Object res = null;
            try {
                res = method.invoke(method, ctx);
            } catch (Exception e) {
                throw new VertxException(e);
            }
            if(res == null){
                throw new VertxException("method return is null");
            }
            if(res instanceof String) {
                ResKit.JSON(ctx,200,res.toString());
            }
            for (MessageConvertor messageConvertor : messageConvertorList) {
                if (messageConvertor.support(res)) {
                    messageConvertor.convertTo(res).handle(ctx);
                    return;
                }
            }
            throw new VertxException("method return is not supported");
        };
    }
    // template wrapper provider
    public static Handler<RoutingContext> templateWrapper(VertxHttpServerConfig serverConfig, Method method, Object instance, TemplateBody templateBody) {
        return ctx->{
            Map<String, AbstractTemplateEngineDelegate> templateEngineMap = serverConfig.getTemplateEngineMap();
            if (templateEngineMap == null) {
                throw new VertxException("templateEngineMap is empty");
            }
            String engineName = templateBody.engineName();
            AbstractTemplateEngineDelegate templateEngine = null;
            // check for template engine
            if (StringKit.isEmpty(engineName) && templateEngineMap.containsKey(engineName)) {
                templateEngine = templateEngineMap.get(engineName);
            }
            String templateFilePath = null;
            try {
                // template engine search order
                // 1. select specified
                // 2. select based on suffix
                JsonObject dataModel = new JsonObject();
                Object res = method.invoke(instance, ctx, dataModel);
                if (res == null) return ;
                templateFilePath = res.toString();
                if (templateEngine != null) {
                    doRender(templateEngine,dataModel,templateFilePath,ctx);
                }else {
                    String templateSuffix = TemplateKit.templateSuffix(templateFilePath);
                    for (AbstractTemplateEngineDelegate engine : templateEngineMap.values()) {
                        if(engine.support(templateSuffix)) {
                            doRender(engine,dataModel,templateFilePath,ctx);
                            return ;
                        }
                    }
                }
                throw new VertxException("template engine not support " + engineName);
            }catch (VertxException e) {
                throw e;
            }catch (Exception e) {
                throw new VertxException(e);
            }
        };
    }
    private static void doRender(AbstractTemplateEngineDelegate templateEngine,JsonObject dataModel,String templateFilePath, RoutingContext ctx) {
        templateEngine.render(dataModel, TemplateKit.buildTemplatePath(templateEngine.getBasePath(),templateFilePath),res->{
            if(res.succeeded()) {
                ctx.response().end(res.result());
                return;
            }
            throw new VertxException(res.cause());
        });
    }
}


