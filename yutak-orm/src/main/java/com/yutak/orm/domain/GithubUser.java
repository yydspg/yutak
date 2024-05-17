package com.yutak.orm.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/*
 * <p>
 * 
 * </p>
 *
 * @author paul
 * @since 2024-05-17
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("github_user")
public class GithubUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /*
     * 用户 ID
     */
    private Long id;

    /*
     * 登录名
     */
    private String login;

    /*
     * 节点ID
     */
    private String nodeId;

    /*
     * 头像URL
     */
    private String avatarUrl;

    /*
     * Gravatar ID
     */
    private String gravatarId;

    /*
     * GitHub URL
     */
    private String url;

    /*
     * GitHub HTML URL
     */
    private String htmlUrl;

    /*
     * 关注者URL
     */
    private String followersUrl;

    /*
     * 被关注者URL
     */
    private String followingUrl;

    /*
     * 代码片段URL
     */
    private String gistsUrl;

    /*
     * 收藏URL
     */
    private String starredUrl;

    /*
     * 订阅URL
     */
    private String subscriptionsUrl;

    /*
     * 组织URL
     */
    private String organizationsUrl;

    /*
     * 仓库URL
     */
    private String reposUrl;

    /*
     * 事件URL
     */
    private String eventsUrl;

    /*
     * 接收事件URL
     */
    private String receivedEventsUrl;

    /*
     * 用户类型
     */
    private String type;

    /*
     * 是否为管理员
     */
    private Boolean siteAdmin;

    /*
     * 姓名
     */
    private String name;

    /*
     * 公司
     */
    private String company;

    /*
     * 博客
     */
    private String blog;

    /*
     * 所在地
     */
    private String location;

    /*
     * 电子邮件
     */
    private String email;

    /*
     * 是否可被雇佣
     */
    private Boolean hireable;

    /*
     * 个人简介
     */
    private String bio;

    /*
     * Twitter 用户名
     */
    private String twitterUsername;

    /*
     * 公共仓库数量
     */
    private Integer publicRepos;

    /*
     * 公共代码片段数量
     */
    private Integer publicGists;

    /*
     * 关注者数量
     */
    private Integer followers;

    /*
     * 被关注者数量
     */
    private Integer following;

    /*
     * 创建时间
     */
    private String githubCreatedAt;

    /*
     * 更新时间
     */
    private String githubUpdatedAt;

    /*
     * 私有代码片段数量
     */
    private Integer privateGists;

    /*
     * 私有仓库总数
     */
    private Integer totalPrivateRepos;

    /*
     * 拥有的私有仓库数量
     */
    private Integer ownedPrivateRepos;

    /*
     * 磁盘使用量
     */
    private Integer diskUsage;

    /*
     * 协作者数量
     */
    private Integer collaborators;

    /*
     * 是否启用两步验证
     */
    private Boolean twoFactorAuthentication;

    /*
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /*
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updatedAt;
}
