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
public class Reminders implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /*
     * 频道ID
     */
    private String channelId;

    /*
     * 频道类型
     */
    private Short channelType;

    /*
     * 提醒类型 1.有人@我 2.草稿
     */
    private Integer reminderType;

    /*
     * 提醒的用户uid，如果此字段为空则表示 提醒项为整个频道内的成员
     */
    private String uid;

    /*
     * 提醒内容
     */
    private String text;

    /*
     * 自定义数据
     */
    private String data;

    /*
     *  是否需要定位
     */
    private Short isLocate;

    /*
     * 消息序列号
     */
    private Long messageSeq;

    /*
     * 消息唯一ID（全局唯一）
     */
    private String messageId;

    /*
     *  数据版本
     */
    private Long version;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updatedAt;

    /*
     * 消息client msg no
     */
    private String clientMsgNo;

    /*
     * 是否被删除
     */
    private Short isDeleted;

    /*
     * 提醒项发布者uid
     */
    private String publisher;
}
