package com.HongyuanWang.learningforum.model.dto.auth;

import lombok.Data;
import java.io.Serializable;

/**
 * Google OAuth登录请求
 */
@Data
public class GoogleLoginRequest implements Serializable {

    /**
     * Google ID Token
     */
    private String idToken;

    private static final long serialVersionUID = 1L;
}