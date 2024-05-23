package com.yutak.im.core;

import java.time.Duration;
import java.time.LocalDateTime;

public class Options {
    public long ID            ;      // 节点ID
    public String Mode        ;      // 模式 debug 测试 release 正式 bench 压力测试
    public String HTTPAddr    ;      // http api的监听地址 默认为 0.0.0.0:5001
    public String Addr        ;      // tcp监听地址 例如：tcp://0.0.0.0:5100
    public String RootDir     ;      // 根目录
    public String DataDir     ;      // 数据目录
//    public String GinMode     ;      // gin框架的模式
    public String WSAddr      ;      // websocket 监听地址 例如：ws://0.0.0.0:5200
    public String WSSAddr     ;      // wss 监听地址 例如：wss://0.0.0.0:5210 websocket security
    public boolean tokenAuthOn;  // 是否开启token验证 不配置将根据mode属性判断 debug模式下默认为false release模式为true
    public WSSConfig wssConfig;
    public MonitorConfig monitorConfig;
    public ChannelConfig channelConfig;
    public ConversationConfig conversationConfig;
    public ExternalConfig externalConfig;
    public ManagerCount managerCount;
    public Duration maxIdle = Duration.ofDays(1);
    public Options() {

    }
    public static class WSSConfig {
        public String certFile;
        public String keyFile;
    }
    public static class MonitorConfig {
        public boolean on;
        public String addr;
    }
    public static class Demo {
        public boolean on;
        public String addr;
    }
    public static class ChannelConfig {
        public int cacheCount;
        public boolean createIfNotExist;
        public int subscriberCompressOfCount;
    }
    public static class ExternalConfig {
        public String IP          ;    // 外网IP
        public String TCPAddr     ;    // 节点的TCP地址 对外公开，APP端长连接通讯  格式： ip:port
        public String WSAddr      ;    //  节点的wsAdd地址 对外公开 WEB端长连接通讯 格式： ws://ip:port
        public String WSSAddr     ;    // 节点的wssAddr地址 对外公开 WEB端长连接通讯 格式： wss://ip:port
        public String MonitorAddr ;    // 对外访问的监控地址
        public String APIUrl      ;    // 对外访问的API基地址 格式: http://ip:port
    }
    public static class ConversationConfig {
        public boolean recentOn             ;// 是否开启最近会话
        public int cacheExpire              ;// 最近会话缓存过期时间 (这个是热数据缓存时间，并非最近会话数据的缓存时间)
        public int syncInterval             ;// 最近会话同步间隔
        public int syncOnce                 ;//  当多少最近会话数量发送变化就保存一次
        public int userMaxCount             ;// 每个用户最大最近会话数量 默认为500
    }
    public static class ManagerCount {
        public String token   ; // 管理者的token
        public String UID     ; // 管理者的uid
        public boolean on ; // 管理者的token是否开启
    }
}
