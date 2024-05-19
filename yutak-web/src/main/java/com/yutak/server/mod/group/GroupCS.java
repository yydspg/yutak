package com.yutak.server.mod.group;

public interface GroupCS {


    interface memberRole{ // 群成员角色
        int creator = 0; // 群主
        int manager = 1; // 管理原
        int normal = 2; // 成员
    }
    interface AllowViewHistoryMsg { // 允许新用户查看消息
        short ok = 0; // 允许
        short deny = 1; // 拒绝
    }
     interface attribute {
        String name = "name"; //群名称
        String notice = "notice"; //群公告
        String forbidden = "forbidden"; // 群禁言
        String invite = "invite"; // 邀请确认
        String forbiddenAddFriend = "forbiddenAddFriend"; //禁止加好友
        String status = "status"; //群状态
        String allowViewHistory = "allowViewHistory"; // 允许查看历史消息
    }
    interface status {
        short normal = 0;
        short disabled = 1; // 禁用
        short disband = 2; // 解散
    }
    interface type {
        short common = 0;
        short large = 1;
    }
    interface memberStatus {
        short normal = 1;
        short block = 2;
    }

}











