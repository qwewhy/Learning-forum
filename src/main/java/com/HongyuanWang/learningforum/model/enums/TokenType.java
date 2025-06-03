package com.HongyuanWang.learningforum.model.enums;

public enum TokenType {
    EMAIL_VERIFICATION("email_verification", "邮箱验证"),
    PASSWORD_RESET("password_reset", "密码重置");

    private final String value;
    private final String description;

    TokenType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}