## 比赛报名
赛道为： 解码赛道; 赛题： 个性化消息收发系统; contract: 19102821364
## 赛题解读
用户维度，理想的情况应该是 存在独立的空间来持久化数据，但是数据碎片化，过于灵活，所以本人认为 类似于bilibili/douyin的消息推送是比较好的选择，即：发送提醒消息（携带一个路径），用户点击后，查询消息内容,
这样服务器的功能就是：根据channel进行消息推送，这样只用持久化每个channel的subscribers，从channel这个维度来看是正确的,
如果引入消息队列,让消费者延迟消费，引入中间件会增加运维难度，所以开发一个IM系统应该是较优的方案
## 项目技术
java编写，采用vertx作为基础框架，对外暴露的http/tcp均为响应式接口,
vertx-web此框架基于netty,所以对tcp和websocket的支持完善,对Buffer操作就可以定义协议,
关于抽象的 channel,这个可以表示系统,组,人与人.etc,所以这里这后很容易扩展,
对于各种需要持久化的数据，比如 channel的 subscribers,info,user的token,system的BlockIp,whiteIP这些数据很碎片，不可能用关系型DB,考虑过mongodb,ck .etc但是根据本人了解到的都不适合,
核心的channel是LRU缓存的，但是我重构了LRU的实现，从加锁的linkedHashMap,转为多组 ConcurrentHashMap,
最合适的应该是K-V型DB,比如redis,最后我选择 rocksDB,写多读少,对其所有api提供项目内部的异步接口
## 提交记录
- 2024.6.21
### info
目前收发消息的链路完善,关于channel,user,system.message的http接口完善,提供简易的client测试
### tip
目前项目对于 android 端,部署,命令启动,websocket连接 .etc方向均 **不支持**,测试建议idea直接启动，对于client,最好内网分开启动(也就是**yutak-im**的ip和client的不一致),
本人 manjaro(gnome) 本机环境启动client,会存在client自动close现象,
对于一个ip,只能启动一个**client**,connect.id是根据 Ip -4 转为 long,即使通过自定义connect.uid作为区分,实际的netSocket也会只表示同一对象(如果启动多个，会导致client只存在一个netClient,数据乱码),
建立连接后,请求 http://localhost:10001/message/send ,给出一个可行的json,之后在控制台显示出信息
```json
{
    "header":{
        "noPersist":1,
        "redDot":0,
        "syncOnce":0
    },
    "clientMsgNo":"",
    "streamNo":null,
    "fromUID":"23",
    "channelID":"",
    "channelType":0,
    "expire":0,
    "payload":"www.baidu.com",
    "subscribers":[
        "0",
        "1",
        "2",
        "3"
    ]
}
```
