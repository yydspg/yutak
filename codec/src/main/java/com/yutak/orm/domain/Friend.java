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
public class Friend implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /*
     * 用户UID
     */
    private String uid;

    /*
     * 好友uid
     */
    private String toUid;

    /*
     * 对好友的备注 TODO: 此字段不再使用，已经迁移到user_setting表
     */
    private String remark;

    /*
     * 好友标示
     */
    private Short flag;

    /*
     * 版本号
     */
    private Long version;

    /*
     * 验证码 加好友来源
     */
    private String vercode;

    /*
     * 好友来源
     */
    private String sourceVercode;

    /*
     * 是否已删除
     */
    private Short isDeleted;

    /*
     * 单项好友
     */
    private Short isAlone;

    /*
     * 加好友发起方
     */
    private Short initiator;

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
