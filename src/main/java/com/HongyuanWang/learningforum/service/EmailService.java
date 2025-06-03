package com.HongyuanWang.learningforum.service;

public interface EmailService {
    /**
     * 发送邮箱验证邮件
     */
    void sendVerificationEmail(String to, String token, String userName);

    /**
     * 发送密码重置邮件
     */
    void sendPasswordResetEmail(String to, String token, String userName);

    /**
     * 发送欢迎邮件（Google OAuth用户）
     */
    void sendWelcomeEmail(String to, String userName);
}
