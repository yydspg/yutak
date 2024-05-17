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
@TableName("group_setting")
public class GroupSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String uid;

    private String groupNo;

    private String remark;

    private Short mute;

    private Short top;

    private Short showNick;

    private Short save;

    private Short chatPwdOn;

    private Short revokeRemind;

    private Short joinGroupRemind;

    private Short screenshot;

    private Short receipt;

    private Long version;

    /*
     * 阅后即焚是否开启 1.开启 0.未开启
     */
    private Short flame;

    /*
     * 阅后即焚销毁秒数
     */
    private Short flameSecond;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updatedAt;
}
