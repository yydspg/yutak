package com.yutak.orm.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("conversation_extra")
public class ConversationExtra implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /*
     * 所属用户
     */
    private String uid;

    /*
     * 频道ID
     */
    private String channelId;

    /*
     * 频道类型
     */
    private Short channelType;

    /*
     * 预览到的位置，与会话保持位置不同的是 预览到的位置是用户读到的最大的messageSeq。跟未读消息数量有关系
     */
    private Long browseTo;

    /*
     * 会话保持的位置
     */
    private Long keepMessageSeq;

    /*
     * 会话保持的位置的偏移量
     */
    private Integer keepOffsetY;

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

    /*
     * 草稿
     */
    private String draft;

    /*
     * 数据版本
     */
    private Long version;
}
