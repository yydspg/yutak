package com.yutak.server.mod.group;

import com.yutak.orm.service.GroupSettingService;
import com.yutak.vertx.anno.RouteHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RouteHandler("/group/manage")
public class GroupManageApi {

    private final GroupSettingService groupSettingService;

}
