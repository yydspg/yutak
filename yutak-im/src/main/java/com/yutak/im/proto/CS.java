package com.yutak.im.proto;

public interface CS {
    interface FrameType {
        byte UNKNOWN    =0;                    // 保留位
        byte CONNECT    =1;                // 客户端请求连接到服务器(c2s)
        byte CONNACK    =2;                // 服务端收到连接请求后确认的报文(s2c)
        byte SEND       =3;                // 发送消息(c2s)
        byte SENDACK    =4;                // 收到消息确认的报文(s2c)
        byte RECV       =5;                // 收取消息(s2c)
        byte RECVACK    =6;                // 收取消息确认(c2s)
        byte PING       =7;                //ping请求
        byte PONG       =8;                // 对ping请求的相应
        byte DISCONNECT =9;                // 请求断开连接
        byte SUB        =10;                // 订阅
        byte SUBACK     =11;                // 订阅确认
    }
    interface ReasonCode {
        // ReasonSuccess 成功
        byte ReasonSuccess                                      = 0;
        // ReasonAuthFail 认证失败
        byte         ReasonAuthFail                             = 0;
        // ReasonSubscriberNotExist 订阅者在频道内不存在
        byte ReasonSubscriberNotExist                           = 0;
        // ReasonInBlacklist 在黑名单列表里
        byte         ReasonInBlacklist                          = 0;
        // ReasonChannelNotExist 频道不存在
        byte ReasonChannelNotExist                              = 0;
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
        byte         ReasonNotSupportHeader                       = 0;
        // clientKey 是空的
        byte ReasonClientKeyIsEmpty                               = 0;
        // 速率限制
        byte         ReasonRateLimit                               = 0;
        byte ReasonNotSupportChannelType                           = 0;// 不支持的频道类型
        byte         ReasonDisband                                   = 0;         // 频道已解散
    }
}
