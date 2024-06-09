package com.yutak.server.mod.group;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yutak.orm.domain.Group;
import com.yutak.orm.domain.GroupMember;
import com.yutak.orm.domain.User;
import com.yutak.orm.service.GroupInviteService;
import com.yutak.orm.service.GroupMemberService;
import com.yutak.orm.service.GroupService;
import com.yutak.orm.service.UserService;
import com.yutak.vertx.kit.UUIDKit;
import com.yutak.server.mod.core.CommonCS;
import com.yutak.server.mod.core.YutakException;
import com.yutak.server.request.CreateGroup;
import com.yutak.vertx.anno.RouteHandler;
import com.yutak.vertx.anno.RouteMapping;
import com.yutak.vertx.core.HttpMethod;
import com.yutak.vertx.kit.ResKit;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.transaction.TransactionProperties;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RouteHandler("/group")
@Component
public class GroupApi {

    private final GroupService groupService;
    private final GroupInviteService groupInviteService;
    private final GroupMemberService groupMemberService;
    private final UserService userService;
    private final DataSourceTransactionManager transactionManager;
    private final TransactionProperties transactionProperties;

    @RouteMapping(path = "/create",method = HttpMethod.POST,block = true)
    public Handler<RoutingContext> createGroup() {
        return ctx -> {
//            String uid = ctx.request().getParam("uid");
//            String groupName = ctx.request().getParam("groupName");
            // TODO  :  这里有水平越权问题，以后慢慢优化
            String uid = UUID.randomUUID().toString();
            CreateGroup group = ctx.body().asPojo(CreateGroup.class);
            if(group == null ) {
                ctx.end();
                return;
            }

            // TODO  :  add appConfig check
            // check create user whether exists
            User creator = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUid, uid));
            if(creator == null) {
                ResKit.error(ctx,"user not found");
                return;
            }

            //add create user into uids
            List<String> uids = group.getUids();
            uids.add(uid);
            group.setUids(uids);
            // check users
            List<User> users = userService.list(new LambdaQueryWrapper<User>().eq(User::getUid, uid));
            if(users == null || users.size() == 0) {
                ResKit.error(ctx,"uids not correct");
                return ;
            }
            if(group.getGroupName() == null || group.getGroupName().length() == 0) {
                //automatic build group name
                StringBuilder sb= new StringBuilder(creator.getName());
                users.forEach(t-> {
                    if(sb.length() >= 20 ) return;
                    sb.append(t.getName()).append(",");
                });
                group.setGroupName(sb.toString());
            }
            // build group number
            String groupNum = UUIDKit.get();

            // TODO  :  生成序列号 并 注册服务 ？？ 没看懂

            // execute module
            // begin transaction
            TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
            // build data
            Group record = new Group();
            record.setCreator(creator.getUid());
            record.setGroupNo(groupNum);
            record.setName(group.getGroupName());
            record.setGroupType(GroupCS.type.common);
            record.setAllowViewHistoryMsg(GroupCS.AllowViewHistoryMsg.ok);
            ArrayList<String> realUsers = new ArrayList<>();
            Group res = null;
            try {
                groupService.save(record);
                ArrayList<DestroyUser> destroyUsers = new ArrayList<>();
                for (User t : users) {
                    if(t.getIsDestroy() == 1) {
                        DestroyUser d = new DestroyUser();
                        d.name = t.getName();
                        d.uid = t.getUid();
                        destroyUsers.add(d);
                        continue;
                    }
                    realUsers.add(t.getUid());
                    // TODO  : 生成version
                    short role = 2;
                    if(t.getUid().equals(creator.getUid())) role = 0;
                    GroupMember g = new GroupMember();
                    g.setGroupNo(groupNum);
                    g.setUid(t.getUid());
                    g.setRole(role);
                    g.setInviteUid(creator.getUid());
                    g.setStatus(GroupCS.memberStatus.normal);
                    g.setVercode(UUIDKit.get()+CommonCS.vercodeType.groupMember);
                    groupMemberService.save(g);
                }
                if(realUsers.size() == 0) {
                    throw new YutakException("user list is empty");
                }
                // publish group create event
                // TODO  :  了解下这个go 的代码！
                // publish destroy member can get in group event
                // TODO  :  同上，还有很多未解决的部分
                res = groupService.getOne(new LambdaQueryWrapper<Group>().eq(Group::getGroupNo, groupNum));
                transactionManager.commit(transaction);
            }catch (Exception e) {
                ResKit.error(ctx,e.getMessage());
                return ;
            }
            ResKit.success(ctx,res);
        };
    }
    private class DestroyUser {
        public String uid;
        public String name;
    }
}
