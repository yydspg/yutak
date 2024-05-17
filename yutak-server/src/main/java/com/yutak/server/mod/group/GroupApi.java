package com.yutak.server.mod.group;

import com.yutak.orm.domain.User;
import com.yutak.orm.service.GroupInviteService;
import com.yutak.orm.service.GroupMemberService;
import com.yutak.orm.service.GroupService;
import com.yutak.server.request.CreateGroup;
import com.yutak.vertx.anno.RouteHandler;
import com.yutak.vertx.anno.RouteMapping;
import com.yutak.vertx.core.HttpMethod;
import com.yutak.vertx.kit.ReqKit;
import com.yutak.vertx.kit.ResKit;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RouteHandler("/group")
@Component
public class GroupApi {

    private final GroupService groupService;
    private final GroupInviteService groupInviteService;
    private final GroupMemberService groupMemberService;

    @RouteMapping(path = "/create",method = HttpMethod.POST)
    public Handler<RoutingContext> createGroup() {
        return ctx -> {
//            String uid = ctx.request().getParam("uid");
//            String groupName = ctx.request().getParam("groupName");
            // TODO  :  这里有水平越权问题，以后慢慢优化
            JsonObject json = ReqKit.getJSON(ctx);
            String uid = json.getString("uid");
            System.out.println(json.toString());
            JsonObject jsonObject = ctx.body().asJsonObject();

//            CreateGroup createGroup = jsonObject.mapTo(CreateGroup.class);
//            System.out.println(createGroup);
            System.out.println(jsonObject);
            CreateGroup createGroup = ctx.body().asPojo(CreateGroup.class);
            System.out.println(createGroup.getGroupName());
            System.out.println(createGroup.getUids());
            if(uid == null ||createGroup == null ) {
                ctx.end();
                return;
            }

//            // TODO  :  add appConfig check
//            // check create user whether exists
//            User user = userMapper.selectById(uid);
//            if(user == null) return "no such user";
//            //add create user into uids
//            List<String> uids = group.getUids();
//            uids.add(uid);
//            group.setUids(uids);
//            // check users
//            List<User> users = userMapper.selectBatchIds(group.getUids());
//            if(users == null || users.size() == 0) return "uids no correct";
//            if(group.getGroupName() == null || group.getGroupName().length() == 0) {
//                //automatic build group name
//                StringBuilder sb= new StringBuilder(username);
//                users.forEach(t-> {
//                    if(sb.length() >= 20 ) return;
//                    sb.append(t.getName()).append(",");
//                });
//                group.setGroupName(sb.toString());
//            }
//            String groupNum = UUID.randomUUID().toString().replace("-", "");
            return ;
        };
    }

}
