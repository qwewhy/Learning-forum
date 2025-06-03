package com.HongyuanWang.learningforum.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * users
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * account
     */
    private String userAccount;

    /**
     * key
     */
    private String userPassword;

    /**
     * Wechat public platform id
     */
    private String unionId;

    /**
     * Wechat public account openId
     */
    private String mpOpenId;

    /**
     * user name
     */
    private String userName;

    /**
     * user avatar
     */
    private String userAvatar;

    /**
     * user profile
     */
    private String userProfile;

    /**
     * user role：user/admin/ban
     */
    private String userRole;

    /**
     * edit time
     */
    private Date editTime;

    /**
     * create time
     */
    private Date createTime;

    /**
     * update time
     */
    private Date updateTime;

    /**
     * is delete (0: no, 1: yes)
     */
    @TableLogic
    private Integer isDelete;

    /**
     * vip expiration time
     */
    private Date vipExpireTime;

    /**
     * vip code
     */
    private String vipCode;

    /**
     * vip number
     */
    private Long vipNumber;

    /**
     * Stripe Customer ID
     */
    private String stripeCustomerId;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 年级
     */
    private String grade;

    /**
     * 工作经验
     */
    private String workExperience;

    /**
     * 擅长方向
     */
    private String expertiseDirection;

    /**
     * 邮箱是否已验证
     */
    private Boolean activated;

    /**
     * Google OAuth ID
     */
    private String googleId;

    /**
     * 邮箱验证时间
     */
    private Date emailVerifiedAt;

    /**
     * 认证类型：password/google
     */
    private String authType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}