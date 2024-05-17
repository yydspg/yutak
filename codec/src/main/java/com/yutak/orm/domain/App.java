package com.yutak.orm.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
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
public class App implements Serializable {

    private static final long serialVersionUID = 1L;

    /*
     * app id
     */
    private String appId;

    /*
     * app key
     */
    private String appKey;

    /*
     * 状态 0.禁用 1.可用
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updatedAt;

    /*
     * app名字
     */
    private String appName;

    /*
     * app logo
     */
    private String appLogo;
}
