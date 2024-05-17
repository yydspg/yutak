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
@TableName("gitee_user")
public class GiteeUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /*
     * 用户 ID
     */
    private Long id;

    /*
     * 用户名
     */
    private String login;

    /*
     * 用户姓名
     */
    private String name;

    /*
     * 用户邮箱
     */
    private String email;

    /*
     * 用户简介
     */
    private String bio;

    /*
     * 用户头像 URL
     */
    private String avatarUrl;

    /*
     * 用户博客 URL
     */
    private String blog;

    /*
     * 用户事件 URL
     */
    private String eventsUrl;

    /*
     * 用户粉丝数
     */
    private Integer followers;

    /*
     * 用户粉丝 URL
     */
    private String followersUrl;

    /*
     * 用户关注数
     */
    private Integer following;

    /*
     * 用户关注 URL
     */
    private String followingUrl;

    /*
     * 用户 Gist URL
     */
    private String gistsUrl;

    /*
     * 用户主页 URL
     */
    private String htmlUrl;

    /*
     * 用户角色
     */
    private String memberRole;

    /*
     * 用户组织 URL
     */
    private String organizationsUrl;

    /*
     * 用户公开 Gist 数
     */
    private Integer publicGists;

    /*
     * 用户公开仓库数
     */
    private Integer publicRepos;

    /*
     * 用户接收事件 URL
     */
    private String receivedEventsUrl;

    /*
     * 企业备注名
     */
    private String remark;

    /*
     * 用户仓库 URL
     */
    private String reposUrl;

    /*
     * 用户收藏数
     */
    private Integer stared;

    /*
     * 用户收藏 URL
     */
    private String starredUrl;

    /*
     * 用户订阅 URL
     */
    private String subscriptionsUrl;

    /*
     * 用户 URL
     */
    private String url;

    /*
     * 用户关注的仓库数
     */
    private Integer watched;

    /*
     * 用户微博 URL
     */
    private String weibo;

    /*
     * 用户类型
     */
    private String type;

    /*
     * gitee用户创建时间
     */
    private String giteeCreatedAt;

    /*
     * gitee用户更新时间
     */
    private String giteeUpdatedAt;

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
