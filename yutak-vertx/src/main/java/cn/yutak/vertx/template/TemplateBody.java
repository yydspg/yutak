package cn.yutak.vertx.template;

import cn.yutak.vertx.core.VertxCS;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TemplateBody {
    /**
     * 解析引擎名称
     */
    String engineName() default VertxCS.DEFAULT_TEMPLATE_ENGINE_NAME;

}
