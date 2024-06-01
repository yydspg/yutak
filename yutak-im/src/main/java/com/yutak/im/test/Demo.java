package com.yutak.im.test;


import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.util.concurrent.TimeUnit;

public class Demo {
    public Handler<String> d1(String str, Vertx vertx) {
        return  s->{

            System.out.println(s);
            System.out.println(str);
            vertx.executeBlocking(p->{

            });
        };
    }
    public Handler<String> d2(String str) {
        return  s->{
            System.out.println(s);
            System.out.println(str);
        };
    }
    public Handler<String> d3(String str) {
        return  s->{
            System.out.println(s);
            System.out.println(str);
        };
    }
}
