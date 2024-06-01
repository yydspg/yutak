package com.yutak.im.kit;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SocketKit {
    public static long ipToLong(String ip) {
        String[] parts = ip.split("\\.");
        long result = 0;
        for (int i = 0; i < parts.length; i++) {
            int part = Integer.parseInt(parts[i]);
            result |= ((long) part << (8 * (3 - i)));
        }
        return result;
    }
    public static void stop(NetSocket s,String msg) {
        Buffer buffer = Buffer.buffer();
        buffer.setString(0, msg);
    }
    public static void main(String[] args) {
        ScheduledThreadPoolExecutor s = new ScheduledThreadPoolExecutor(1);
        AtomicInteger a = new AtomicInteger();
        final long l = System.currentTimeMillis();
        s.scheduleAtFixedRate(()->{
            Random random = new Random();
            // 生成四个字节的随机数
            int firstByte = random.nextInt(256); // 第一个字节可以从0到255
            int secondByte = random.nextInt(256);
            int thirdByte = random.nextInt(256);
            int fourthByte = random.nextInt(256);
            // 拼接成IPv4地址格式
            String ip = firstByte + "." + secondByte + "." + thirdByte + "." + fourthByte;
            a.getAndIncrement();
            if(a.get() == 10000) {
                System.out.println("testOK");
                System.out.println(System.currentTimeMillis()-l);
            }
        },1,1, TimeUnit.MILLISECONDS);

    }
}
