package com.yutak.im.test;


import com.yutak.im.core.ChannelManager;
import com.yutak.im.domain.Channel;
import com.yutak.im.proto.CS;
import com.yutak.im.store.H2Store;
import com.yutak.im.store.Store;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import javax.swing.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Demo {
    public Handler<String> d1(String str, Vertx vertx) {
        return  s->{

            System.out.println(s);
            System.out.println(str);
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

    public static void main(String[] args) {
        // Db test

        H2Store h = H2Store.get();
        ChannelManager channelManager = ChannelManager.get();
        ScheduledThreadPoolExecutor s = new ScheduledThreadPoolExecutor(1);


        int i =0;
        while(i<1000) {
            long l = System.currentTimeMillis();
            Channel channel = channelManager.getChannel("1234233", CS.ChannelType.Data);
            long l1 = System.currentTimeMillis();
            System.out.println("第" +i+"次测试时间");
            System.out.println(l1-l);
            i++;
        }

    }
}
