package com.HongyuanWang.learningforum.model.dto.auth;

import lombok.Data;
import java.io.Serializable;

/**
 * 邮箱注册请求
 */
@Data
public class EmailRegisterRequest implements Serializable {

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;

    private static final long serialVersionUID = 1L;
}