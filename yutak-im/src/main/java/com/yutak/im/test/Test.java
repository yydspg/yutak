package com.yutak.im.test;

import com.zaxxer.hikari.HikariConfig;
import io.vertx.core.Handler;
import io.vertx.core.net.NetSocket;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author paul 2024/5/30
 */

public  class Test implements Handler<NetSocket> {
    private Test(){}

    private final static   Test  t = new Test() ;
    public static Test get() {
        return t;
    }
    @Override
    public void handle(NetSocket netSocket) {
        System.out.println("test");
        Class<? extends Test> aClass = this.getClass();

        HikariConfig h = new HikariConfig();

        DataSource dataSource = h.getDataSource();

    }
}