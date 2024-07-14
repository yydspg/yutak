## 比赛报名
赛道为： 解码赛道; 赛题： 个性化消息收发系统; contract: 19102821364
## 赛题解读
用户维度，理想的情况应该是 存在独立的空间来持久化数据，但是数据碎片化，过于灵活，所以本人认为 类似于bilibili/douyin的消息推送是比较好的选择，即：发送提醒消息（携带一个路径），用户点击后，查询消息内容,
这样服务器的功能就是：根据channel进行消息推送，这样只用持久化每个channel的subscribers，从channel这个维度来看是正确的,
如果引入消息队列,让消费者延迟消费，引入中间件会增加运维难度，所以开发一个IM系统应该是较优的方案
## 项目技术
#### 架构
本项目采用java(JDK17)编写，采用vertx和vertx-web作为基础框架.此框架基于netty,所以对tcp和websocket的支持完善,对Buffer操作就可以定义协议,
yutak-vertx此部分为本人对vertx的适配框架,使用和springboot类似,做到了路由收集,拦截器管理.etc
对于各种需要持久化的数据，比如 channel的 subscribers,info,user的token,system的BlockIp,whiteIP这些数据很碎片，使用用关系型DB很困难,考虑过mongodb,ck.etc，但是根据本人所学应该不适合,
最合适的应该是K-V型DB,比如redis(虽然redis存在aof和rdb,但是纯内存数据库在此场景下不适合),最后本人选择 rocksDB,也是唯一提供java api的嵌入式数据库，存在列族的概念，这和bolt的bucket概念相似,写多读少,
本人集成入系统,做了异步的适配
对于消息存储,目前并未使用常用的数据库,其实也可以使用rocksdb来存储,但是为了后期维护,采用更简单的文件追加,以fileChannel和mappedByteBuffer为载体,抽象出 Topic,segment,index,slot.etc来实现message的存储,
不过在序列化上依然存在问题,目前尚未完善
对于index选择内存映射是因为写入数据量少,频繁的从用户态切换之内核态会造成性能损失,
##### 系统概念解释
关于核心的消息包,本人是对tcp的消息体衍生出协议,每个Packet都存在fixHeader
关于channel,抽象为数据收发通道,可以表示系统,组,人与人.etc,此处之后可根据不同业务扩展,
核心的channel是LRU缓存的，但是我重构了LRU的实现，从加锁的linkedHashMap,转为多组 ConcurrentHashMap,(补充：此处经本人验证后存在内存泄漏风险，因此弃用)
proto包即为tcp和websocket的数据包,store包即为存储层,api包为http接口层,domain为对应模型,kit为工具包
##### 系统链路
整体项目的 核心链路是：
1. 数据入口
2. 建立连接(若已建立则跳过)
3. 查询channel
4. 分发Message
5. 数据出口
##### 系统分层
1. data in
2. packet processor
3. delivery
4. data out
5. msg store
6. store
7. manager
##### 对接其他系统
之后对接其他系统的话,统一实现interceptor或者链路中加入webhook就可以
如：在其他系统中使用 send Message,可以选择http接口,广播消息至一个channel,内网之间无需鉴权,从用户维度,使用对应的sdk即可(尚未完成)
对于http接口，不少是直接工作在worker线程的,这也是持久层每个方法都提供同步和异步的原因,易于维护和便于书写,对于核心的tcp和websocket链路,全链路无阻塞，且最大程度的避免了线程切换带来的问题
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
- 2024.7.12
### feat
提供android的SDK,支持websocket,支持消息持久化，支持最近会话，支持流式消息,提供monitor接口，目前接口文档

### refactor
使用 jconsole 工具调试后,发现在较低qps下，自定义的 Cache 依然存在较高的heap 内存占用，导致频繁gc,因此舍弃，全部使用LRU
优化msg的持久化，采用segments和index的模式，所以rocksdb仅用于存储类似 channel的meta信息，对msgStore和Store的method提供sync,和Async的实现,利于上层的接口实现或者二次开发
目前对与全链路未采用 Interceptor的拦截，在yutak-vertx中,这是已经实现的功能,所以对于之后的鉴权,易于升级
![Screenshot from 2024-07-13 22-13-28.png](..%2F..%2FPictures%2FScreenshots%2FScreenshot%20from%202024-07-13%2022-13-28.png)

## summarize
目前系统还是存在较多的bug和设计思路的错误,本人错误估计了工作量和期末课业的压力，致使比赛中途暂停15天之久,但是项目的总体设计正确,以后要对系统整体健壮性提升,目前的架构是不支持分布式的,后续也会想对
一些地方进行重构，希望在未来能达到商用标准，感谢老师百忙之中点评本人的菜鸟作品,诚谢!!