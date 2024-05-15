package com.yutak.vertx.template;

import com.yutak.vertx.core.VertxCS;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface VertxTemplateEngine {
    String name() default VertxCS.DEFAULT_TEMPLATE_ENGINE_NAME;

    String basePath() default VertxCS.DEALUT_TEMPLATE_BASE_PATH;
}