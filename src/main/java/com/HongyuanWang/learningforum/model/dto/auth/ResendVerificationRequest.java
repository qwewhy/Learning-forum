package com.HongyuanWang.learningforum.model.dto.auth;

import lombok.Data;
import java.io.Serializable;

@Data
public class ResendVerificationRequest implements Serializable {
    private String email;
    private static final long serialVersionUID = 1L;
}
