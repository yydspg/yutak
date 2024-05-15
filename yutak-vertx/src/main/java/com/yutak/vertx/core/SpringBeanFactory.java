package com.yutak.vertx.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
@Component
public class SpringBeanFactory implements BeanFactory,ApplicationContextAware {
    private  ApplicationContext applicationContext;
    @Override
    public Object get(Class clazz) {
        return applicationContext.getBean(clazz);
    }

    @Override
    public Set<Object> getTypesAnnotatedWith(Class annotion) {
        Map beans = applicationContext.getBeansWithAnnotation(annotion);
        return new HashSet<>(beans.values());
    }

    @Override
    public void setApplicationContext(ApplicationContext act) throws BeansException {
        this.applicationContext = act;
    }
}
