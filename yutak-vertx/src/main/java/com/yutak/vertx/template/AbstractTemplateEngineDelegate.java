package com.yutak.vertx.template;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.common.template.TemplateEngine;

import java.util.Map;

public abstract class AbstractTemplateEngineDelegate implements TemplateEngine {
    protected TemplateEngine delegate;
    protected String basePath;

    protected Vertx vertx;

    public abstract boolean support(String suffix);

    public abstract void after();
    @Override
    public void render(Map<String, Object> context, String templateFileName, Handler<AsyncResult<Buffer>> handler) {
        this.delegate.render(context, templateFileName, handler);
    }

    @Override
    public void render(JsonObject context, String templateFileName, Handler<AsyncResult<Buffer>> handler) {
        this.delegate.render(context, templateFileName, handler);
    }

    public TemplateEngine getDelegate() {
        return delegate;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setDelegate(TemplateEngine delegate) {
        this.delegate = delegate;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public Vertx getVertx() {
        return vertx;
    }

    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

}
