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
        // ReasonSuccess 成功
        byte ReasonSuccess                                       = 0;
        // ReasonAuthFail 认证失败
        byte         ReasonAuthFail                              = 0;
        // ReasonSubscriberNotExist 订阅者在频道内不存在
        byte ReasonSubscriberNotExist                            = 0;
        // ReasonInBlacklist 在黑名单列表里
        byte         ReasonInBlacklist                           = 0;
        // ReasonChannelNotExist 频道不存在
        byte ReasonChannelNotExist                               = 0;
        // ReasonUserNotOnNode 用户没在节点上
        byte         ReasonUserNotOnNode                         = 0;
        // ReasonSenderOffline // 发送者离线了，这条消息将发不成功
        byte ReasonSenderOffline                                 = 0;
        // ReasonMsgKeyError 消息key错误 说明消息不合法
        byte         ReasonMsgKeyError                           = 0;
        // ReasonPayloadDecodeError payload解码失败
        byte ReasonPayloadDecodeError                            = 0;
        // ReasonForwardSendPacketError 转发发送包失败
        byte         ReasonForwardSendPacketError                = 0;
        // ReasonNotAllowSend 不允许发送消息
        byte ReasonNotAllowSend                                  = 0;
        // ReasonConnectKick 连接被踢
        byte         ReasonConnectKick                           = 0;
        // ReasonNotInWhitelist 没在白名单内
        byte ReasonNotInWhitelist                                = 0;
        // 查询用户token错误
        byte         ReasonQueryTokenError                       = 0;
        // 系统错误
        byte ReasonSystemError                                   = 0;
        // 错误的频道ID
        byte         ReasonChannelIDError                        = 0;
        // ReasonNodeMatchError 节点匹配错误
        byte ReasonNodeMatchError                                = 0;
        // ReasonNodeNotMatch 节点不匹配
        byte         ReasonNodeNotMatch                          = 0;
        // 频道被封禁
        byte ReasonBan                                           = 0;
        // 不支持的header
        byte         ReasonNotSupportHeader                      = 0;
        // clientKey 是空的
        byte ReasonClientKeyIsEmpty                              = 0;
        // 速率限制
        byte         ReasonRateLimit                             = 0;
        byte ReasonNotSupportChannelType                         = 0;// 不支持的频道类型
        byte         ReasonDisband                               = 0;         // 频道已解散
    }

    interface ChannelType {
        public byte Person 		   = 1;// ChannelTypePerson 个人频道
        public byte Group           = 2; // 群组频道
        public byte CustomerService = 3; // 客服频道
        public byte Community       = 4; // 社区频道
        public byte CommunityTopic  = 5; // 社区话题频道
        public byte Info            = 6; // 资讯频道（有临时订阅者的概念，查看资讯的时候加入临时订阅，退出资讯的时候退出临时订阅）
        public byte Data            = 7; // 数据频道
    }

}
