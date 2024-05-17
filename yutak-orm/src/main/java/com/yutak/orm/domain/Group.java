package com.yutak.orm.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class Group implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String groupNo;

    private String name;

    private String creator;

    private Short status;

    /*
     * 群禁言
     */
    private Short forbidden;

    /*
     * 群邀请开关
     */
    private Short invite;

    private Short forbiddenAddFriend;

    private Short allowViewHistoryMsg;

    private Long version;

    /*
     * 群头像是否已经被用户上传
     */
    private Short isUploadAvatar;

    /*
     * 群类型 0.普通群 1.超大群
     */
    private Short groupType;

    /*
     * 群分类
     */
    private String category;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updatedAt;

    private String notice;
}
