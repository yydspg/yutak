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
@TableName("message_extra")
public class MessageExtra implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String messageId;

    private Long messageSeq;

    private String channelId;

    private Short channelType;

    private String fromUid;

    private Short revoke;

    private String revoker;

    private String cloneNo;

    private Long version;

    private Integer readedCount;

    private Short isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updatedAt;

    /*
     * 编辑后的正文
     */
    private String contentEdit;

    /*
     * 编辑正文的hash值，用于重复判断
     */
    private String contentEditHash;

    /*
     * 编辑时间 时间戳（秒）
     */
    private Integer editedAt;
}
