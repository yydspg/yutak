package com.yutak.im.proto;

public interface CS {
    interface FrameType {
        int UNKNOWN    =0;                    // 保留位
        int CONNECT    =0x10;                // 客户端请求连接到服务器(c2s)
        int CONNACK    =0x20;                // 服务端收到连接请求后确认的报文(s2c)
        int SEND       =0x30;                // 发送消息(c2s)
        int SENDACK    =0x40;                // 收到消息确认的报文(s2c)
        int RECV       =0x50;                // 收取消息(s2c)
        int RECVACK    =0x60;                // 收取消息确认(c2s)
        int PING       =0x70;                //ping请求
        int PONG       =0x80;                // 对ping请求的相应
        int DISCONNECT =0x90;                // 请求断开连接
        int SUB        =0xa0;                // 订阅
        int SUBACK     =0xb0;                // 订阅确认
    }
    interface ReasonCode {
        // Success 成功
        byte success                                       = 0;
        // AuthFail 认证失败
        byte         authFail                              = 0;
        // SubscriberNotExist 订阅者在频道内不存在
        byte SubscriberNotExist                            = 0;
        // InBlacklist 在黑名单列表里
        byte         InBlacklist                           = 0;
        // ChannelNotExist 频道不存在
        byte ChannelNotExist                               = 0;
        // UserNotOnNode 用户没在节点上
        byte         UserNotOnNode                         = 0;
        // SenderOffline // 发送者离线了，这条消息将发不成功
        byte SenderOffline                                 = 0;
        // MsgKeyError 消息key错误 说明消息不合法
        byte         MsgKeyError                           = 0;
        // PayloadDecodeError payload解码失败
        byte PayloadDecodeError                            = 0;
        // ForwardSendPacketError 转发发送包失败
        byte         ForwardSendPacketError                = 0;
        // NotAllowSend 不允许发送消息
        byte NotAllowSend                                  = 0;
        // ConnectKick 连接被踢
        byte         ConnectKick                           = 0;
        // NotInWhitelist 没在白名单内
        byte NotInWhitelist                                = 0;
        // 查询用户token错误
        byte         QueryTokenError                       = 0;
        // 系统错误
        byte SystemError                                   = 0;
        // 错误的频道ID
        byte         ChannelIDError                        = 0;
        // NodeMatchError 节点匹配错误
        byte NodeMatchError                                = 0;
        // NodeNotMatch 节点不匹配
        byte         NodeNotMatch                          = 0;
        // 频道被封禁
        byte Ban                                           = 0;
        // 不支持的header
        byte         NotSupportHeader                      = 0;
        // clientKey 是空的
        byte ClientKeyIsEmpty                              = 0;
        // 速率限制
        byte         RateLimit                             = 0;
        byte NotSupportChannelType                         = 0;// 不支持的频道类型
        byte         Disband                               = 0;         // 频道已解散
    }

    interface ChannelType {
        public byte Person 		    = 1;// ChannelTypePerson 个人频道
        public byte Group           = 2; // 群组频道
        public byte CustomerService = 3; // 客服频道
        public byte Community       = 4; // 社区频道
        public byte CommunityTopic  = 5; // 社区话题频道
        public byte Info            = 6; // 资讯频道（有临时订阅者的概念，查看资讯的时候加入临时订阅，退出资讯的时候退出临时订阅）
        public byte Data            = 7; // 数据频道
        public byte System          = 8;

    }
    interface Device {
        interface Level {
            byte master = 1;
            byte slave = 0;
        }

        interface Flag {
            int app = 0;
            int web = 1;
            int pc = 2;
            int sys = 3;
            int all = -1;
        }
    }
    interface Stream {
        byte start = 0;
        byte ing = 1;
        byte end = 2;
    }
    interface Setting {
        byte unknown = 0;
        byte receiptedEnabled = 1 << 1;
        byte signal = 1 << 5;
        byte noEncrypt = 1 << 4;
        byte topic = 1 << 3;
        byte stream = 1 << 2;
    }
}
