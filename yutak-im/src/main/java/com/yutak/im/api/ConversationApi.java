package com.yutak.im.api;

import com.yutak.im.core.ConversationManager;
import com.yutak.im.domain.Conversation;
import com.yutak.im.domain.Req;
import com.yutak.im.domain.Res;
import com.yutak.im.kit.SocketKit;
import com.yutak.im.proto.CS;
import com.yutak.im.store.YutakStore;
import com.yutak.vertx.anno.RouteHandler;
import com.yutak.vertx.anno.RouteMapping;
import com.yutak.vertx.core.HttpMethod;
import com.yutak.vertx.kit.ReqKit;
import com.yutak.vertx.kit.ResKit;
import com.yutak.vertx.kit.StringKit;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.List;


@RouteHandler("/conversation")
public class ConversationApi {
    private final ConversationManager conversationManager;
    private final YutakStore yutakStore;
    public ConversationApi() {
        this.conversationManager = new ConversationManager();
        yutakStore = YutakStore.get();
    }
    // get conversation list
    @RouteMapping(path = "/list",method = HttpMethod.GET,block = true)
    public Handler<RoutingContext> list() {
        return ctx->{
            //check
            String uid = ReqKit.getStrInPath(ctx, "uid");
            if (StringKit.isEmpty(uid)) {
                ResKit.error(ctx,"no user id");
                return;
            }
            List<Conversation> conversations = conversationManager.getConversations(uid, 0, null);
            List<Res.Conversation> conversationAck = new ArrayList<>();
            if(!conversations.isEmpty()) {
                for(Conversation conversation : conversations) {
                    String fakeChannelID = conversation.channelID;
                    if(conversation.channelType == CS.ChannelType.Person) {
                        fakeChannelID = SocketKit.buildFakeK(uid, conversation.channelID);
                    }
                    // load the newest message
//                    yutakStore.loadMessage
                }
            }
        };
    }
    // clear conversation number
    @RouteMapping(path = "/cleanUnread",method = HttpMethod.POST)
    public Handler<RoutingContext> clearUnread() {
        return ctx->{
            Req.CleanConversationUnread r = ReqKit.getObjectInBody(ctx, Req.CleanConversationUnread.class);
            if(r == null) {
                ResKit.error(ctx,"no invalid request");
                return;
            }
            if (StringKit.isEmpty(r.uid) || StringKit.isEmpty(r.channelID)) {
                ResKit.error(ctx,"no data");
                return;
            }
            // load conversation async  r
            conversationManager.getConversationAsync(r.uid,r.channelID,r.channelType).whenComplete((c, e) -> {
                if(e != null) {
                    ResKit.error(ctx,"get conversation failed");
                    return;
                }
                // current conversation not exists
                if(c == null && r.messageSeq > 0) {
                    c = new Conversation();
                    c.channelID = r.channelID;
                    c.channelType = r.channelType;
                    c.UID = r.uid;
                    c.lastMsgSeq = r.messageSeq;
                    conversationManager.addOrUpdateConversation(r.uid,c);
                }else {
                    // exists
                    Conversation conversation = conversationManager.getConversation(r.uid, r.channelID, r.channelType);
                    conversation.unreadCount = 0;
                    if(r.messageSeq > 0) conversation.lastMsgSeq = r.messageSeq;
                }
               ResKit.success(ctx);
            });
        };
    }
    // set conversation number
    @RouteMapping(path = "/setUnread",method = HttpMethod.POST)
    public Handler<RoutingContext> setUnread() {
        return ctx->{
            Req.SetConversationUnread r = ReqKit.getObjectInBody(ctx, Req.SetConversationUnread.class);
            if(r == null) {
                ResKit.error(ctx,"no invalid request");
                return;
            }
            if (StringKit.isEmpty(r.uid) || StringKit.isEmpty(r.channelID)) {
                ResKit.error(ctx,"no data");
                return;
            }
            // load conversation async
            conversationManager.getConversationAsync(r.uid,r.channelID,r.channelType).whenComplete((c, e) -> {
                if(e != null) {
                    ResKit.error(ctx,"get conversation failed");
                    return;
                }
                // current conversation not exists
                if(c == null && r.messageSeq > 0) {
                    c = new Conversation();
                    c.channelID = r.channelID;
                    c.channelType = r.channelType;
                    c.UID = r.uid;
                    c.unreadCount = 0;
                    c.lastMsgSeq = r.messageSeq;
                    conversationManager.addOrUpdateConversation(r.uid,c);
                }else {
                    // exists
                    Conversation conversation = conversationManager.getConversation(r.uid, r.channelID, r.channelType);
                    conversation.unreadCount = r.unread;
                    if(r.messageSeq > 0) conversation.lastMsgSeq = r.messageSeq;
                }
                ResKit.success(ctx);
            });
        };
    }
    // delete conversation
    @RouteMapping(path = "/delete",method = HttpMethod.POST)
    public Handler<RoutingContext> delete() {
        return ctx->{
            Req.DeleteConversation r = ReqKit.getObjectInBody(ctx, Req.DeleteConversation.class);
            if(r == null) {
                ResKit.error(ctx,"no conversation");
                return;
            }
            if (StringKit.isEmpty(r.uid) && StringKit.isEmpty(r.channelID)) {
                ResKit.error(ctx,"no user id or channel id");
                return;
            }
            conversationManager.deleteConversation(r.uid,r.channelID,r.channelType).whenComplete((t,e)->{
                if(e != null) {
                    ResKit.error(ctx,e.getMessage());
                    return;
                }
                ResKit.success(ctx);
            });
        };
    }
    // sync conversation
    @RouteMapping(path = "/sync",method = HttpMethod.POST)
    public Handler<RoutingContext> sync() {
        return ctx->{

        };
    }
    // sync recent message
    @RouteMapping(path = "/syncMessage",method = HttpMethod.POST)
    public Handler<RoutingContext> syncMessage() {
        return ctx->{
            Req.ChannelRecentMsg r = ReqKit.getObjectInBody(ctx, Req.ChannelRecentMsg.class);
            if(r == null) {
                ResKit.error(ctx,"no invalid request");
                return;
            }
            
        };
    }
}
