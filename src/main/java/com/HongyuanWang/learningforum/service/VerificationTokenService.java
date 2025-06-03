package com.HongyuanWang.learningforum.service;

public interface VerificationTokenService {

    /**
     * 创建邮箱验证令牌
     * @param userId 用户ID
     * @return 令牌字符串
     */
    String createEmailVerificationToken(Long userId);

    /**
     * 创建密码重置令牌
     * @param userId 用户ID
     * @return 令牌字符串
     */
    String createPasswordResetToken(Long userId);

    /**
     * 验证邮箱
     * @param token 令牌
     * @return 是否验证成功
     */
    boolean verifyEmail(String token);

    /**
     * 验证密码重置令牌
     * @param token 令牌
     * @return 用户ID，如果令牌无效返回null
     */
    Long validatePasswordResetToken(String token);

    /**
     * 标记令牌为已使用
     * @param token 令牌
     */
    void markTokenAsUsed(String token);

    /**
     * 清理过期的令牌（定时任务调用）
     */
    void cleanExpiredTokens();
}