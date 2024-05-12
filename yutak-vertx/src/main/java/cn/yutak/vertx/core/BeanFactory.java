package cn.yutak.vertx.core;

import java.util.Set;

public interface BeanFactory {
    Object get(Class clazz);

    Set<Object> getTypesAnnotatedWith(Class annotion);

}
