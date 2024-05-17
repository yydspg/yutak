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
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String uid;

    private String name;

    private String shortNo;

    private Short shortStatus;

    private Short sex;

    private Short robot;

    private String category;

    private String role;

    private String username;

    private String password;

    private String zone;

    private String phone;

    private String chatPwd;

    private String lockScreenPwd;

    private Integer lockAfterMinute;

    private String vercode;

    private Short isUploadAvatar;

    private String qrVercode;

    private Short deviceLock;

    private Short searchByPhone;

    private Short searchByShort;

    private Short newMsgNotice;

    private Short msgShowDetail;

    private Short voiceOn;

    private Short shockOn;

    private Short muteOfApp;

    private Short offlineProtection;

    private Long version;

    private Short status;

    private String benchNo;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updatedAt;

    /*
     * email地址
     */
    private String email;

    /*
     * app id
     */
    private String appId;

    /*
     * 是否已销毁
     */
    private Short isDestroy;

    /*
     * 微信openid
     */
    private String wxOpenid;

    /*
     * 微信unionid
     */
    private String wxUnionid;

    /*
     * gitee的用户id
     */
    private String giteeUid;

    /*
     * github的用户id
     */
    private String githubUid;

    /*
     * web3公钥
     */
    private String web3PublicKey;

    /*
     * 消息过期时长(单位秒)
     */
    private Long msgExpireSecond;
}
