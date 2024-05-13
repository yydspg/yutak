package cn.yutak.vertx.kit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
}
