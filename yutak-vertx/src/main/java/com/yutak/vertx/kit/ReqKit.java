package com.yutak.vertx.kit;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

public class ReqKit {


    public static String getStrInPath(RoutingContext r,String k) {
        try {
            return r.request().getParam(k);
        } catch (Exception e) {
            ResKit.error(r,"parse body params error");
        }
        return null;
    }
    public static String getCookie(HttpServerRequest r,String k) {
        return r.getCookie(k).getValue();
    }
    public static JsonObject getJSON(RoutingContext r) {
        return r.body().asJsonObject();
    }
    public static <T> T getObjectInBody(RoutingContext r,Class<T> c) {
        try {
            return r.body().asPojo(c);
        } catch (Exception e) {
            return null;
        }
    }
    @SneakyThrows
    public static <T> T getObjectInForm(HttpServerRequest r, Class<T> c) {
        Field[] fields = c.getDeclaredFields();
        T t = c.getConstructor().newInstance();
        for (Field field : fields) {
            setFieldVInForm(field,t,r);
        }
        return t;
    }
    @SneakyThrows
    private static void setFieldVInForm(Field f, Object o, HttpServerRequest h) {
        f.setAccessible(true);
        Class<?> type = f.getType();
        if(type == String.class) f.set(o,h.getFormAttribute(f.getName()));
        else if(type == int.class) f.set(o,Integer.parseInt(h.getFormAttribute(f.getName())));
        else if(type == boolean.class) f.set(o,Boolean.parseBoolean(h.getFormAttribute(f.getName())));
        else if(type == long.class) f.set(o,Long.parseLong(h.getFormAttribute(f.getName())));
        else if(type == float.class) f.set(o,Float.parseFloat(h.getFormAttribute(f.getName())));
        else if(type == double.class) f.set(o,Double.parseDouble(h.getFormAttribute(f.getName())));
        else if(type == Integer.class) f.set(o,Integer.valueOf(h.getFormAttribute(f.getName())));
        else if(type == Long.class) f.set(o,Long.valueOf(h.getFormAttribute(f.getName())));
        else if(type == Double.class) f.set(o,Double.valueOf(h.getFormAttribute(f.getName())));
        else if(type == Boolean.class) f.set(o,Boolean.valueOf(h.getFormAttribute(f.getName())));
        else if(type == Float.class) f.set(o,Float.valueOf(h.getFormAttribute(f.getName())));
        else if(type == Byte.class) f.set(o,Byte.valueOf(h.getFormAttribute(f.getName())));
        else if(type == Short.class) f.set(o,Short.valueOf(h.getFormAttribute(f.getName())));
    }

    public static void main(String[] args) {
        JsonObject json = new JsonObject();

    }
}
