package com.yutak.server.mod.core;

public interface CommonCS {
    interface Channel { //频道类型
        byte none  = 0; // 未指定
        byte personal = 1; //个人
        byte group = 2; //群
        byte customer = 3; //客服
        byte community = 4; //社区
        byte communityTopic = 5; // 话题
        byte info = 6; // 咨询
    }
    interface SeqKey { // 序列化key
        String groupMember = "groupMember"; //群成员
        String groupSetting = "groupSetting"; //群设置
        String group = "group"; //群
        String userSetting = "userSetting"; //用户设置
        String user = "user"; //用户序列号
        String friend = "friend"; //好友
        String messageExtra = "messageExtra"; //消息扩展序号
        String messageReaction = "messageReaction"; //消息回应序号
        String root = "root"; //机器人序号
        String rootEvent = "rootEvent"; //机器人事件
        String sensitive = "sensitive"; //敏感词
        String reminder = "reminder"; //提醒项
        String syncConversationExtra = "syncConversationExtra"; //同步最近会话扩展
        String prohibit = "prohibited"; // 违禁词
    }
    interface command {
        String channelUpdate = "channelUpdate"; //频道信息更形
        String channelMemberUpdate = "channelMemberUpdate"; //群成员更新
        String conversationUnreadClear = "conversationUnreadClear"; //未读数更新
        String groupAvatarUpdate = "groupAvatarUpdate"; //群头像跟新
        String communityAvatarUpdate = "communityAvatarUpdate"; //社区头像更新
        String communityCoverUpdate = "communityCoverUpdate"; //社区封面更新
        String conversationDelete = "conversationDelete"; //删除最近对话
        String friendRequest = "friendRequest"; //好友申请
        String friendAccept = "friendAccept"; //接受好友
        String friendDelete = "friendDelete"; //删除好友
        String userAvatarUpdate = "userAvatarUpdate"; //用户头像更新
        String typing = "typing"; //输入中
        String online = "online"; //在线状态
        String momentMessage = "momentMessage"; // 动态点赞或评论消息
        String syncMessageExtra = "syncMessageExtra"; // 同步消息扩展数据
        String syncMessageReaction = "syncMessageReaction"; // 同步消息回应数据
        String pcQuit = "pcQuit"; // 退出 pc 登录
        String conversationDeleted = "conversationDeleted"; //最近会话被删除
        String syncReminder = "syncReminder"; // 同步提醒
        String syncConversationExtra = "syncConversationExtra"; //同步最近会话扩展
        String organizationInfoUpdate = "organizationInfoUpdate"; //组织信息更新
        String organizationQuit = "organizationQuit"; // 退出组织
        String organizationJoin = "organizationJoin"; // 加入组织
    }
    interface rtcCallType {
        int audio = 0; //语音通话
        int video = 1; //视频通话
    }
    interface rtcCallResult {
        int cancel = 0; // 取消
        int hangup = 1; // 挂断
        int missed = 2; //未接听
        int refused = 3; //拒绝
    }
    interface vercodeType {
        int user = 0;
        int groupMember = 1;
        int QRCode = 2;
        int friend = 3;
        int mailList = 4;
    }
}
