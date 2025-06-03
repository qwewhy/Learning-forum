package com.HongyuanWang.learningforum.model.dto.auth;

import lombok.Data;
import java.io.Serializable;

/**
 * 邮箱/用户名登录请求
 */
@Data
public class EmailLoginRequest implements Serializable {

    /**
     * 账号（可以是邮箱或用户名）
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    private static final long serialVersionUID = 1L;
}