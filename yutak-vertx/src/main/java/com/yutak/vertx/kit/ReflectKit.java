package com.yutak.vertx.kit;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class ReflectKit {
    public static boolean isClassImplement(Class clz ,Class interfaceClz) {
        Class[] interfaces = clz.getInterfaces();
        for (Class t : interfaces) {
            if (t.equals(interfaceClz)) return true;
        }
        return false;
    }
    public static Set<Method> getMethodsWithAnnotation(Class clz , Class<? extends Annotation> annotation) {
        Method[] methods = clz.getMethods();
        if(methods.length == 0) return Collections.emptySet();
        Set<Method> methodSet = new HashSet<>();
        Arrays.stream(methods).filter(m -> m.isAnnotationPresent(annotation)).forEach(methodSet::add);
        return methodSet;
    }
    public static Reflections getReflections(String packageAddress) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        FilterBuilder filterBuilder = new FilterBuilder();
        String[] addresses = packageAddress.split(",");
        Stream.of(addresses).forEach(str -> configurationBuilder.addUrls(ClasspathHelper.forPackage(str.trim())));
        filterBuilder.includePackage(Arrays.toString(addresses));
        configurationBuilder.filterInputsBy(filterBuilder);
        return new Reflections(configurationBuilder);
    }
}
