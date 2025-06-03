package com.HongyuanWang.learningforum.model.dto.auth;

import lombok.Data;
import java.io.Serializable;

@Data
public class ResetPasswordRequest implements Serializable {
    private String token;
    private String newPassword;
    private String confirmPassword;
    private static final long serialVersionUID = 1L;
}
