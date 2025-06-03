package com.HongyuanWang.learningforum.service.impl;

import com.HongyuanWang.learningforum.common.ErrorCode;
import com.HongyuanWang.learningforum.exception.BusinessException;
import com.HongyuanWang.learningforum.mapper.VerificationTokenMapper;
import com.HongyuanWang.learningforum.mapper.UserMapper;
import com.HongyuanWang.learningforum.model.entity.User;
import com.HongyuanWang.learningforum.model.entity.VerificationToken;
import com.HongyuanWang.learningforum.model.enums.TokenType;
import com.HongyuanWang.learningforum.service.VerificationTokenService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

/**
 * 验证令牌服务实现
 *
 * 这个服务管理所有的验证令牌，包括邮箱验证和密码重置令牌。
 * 它确保了令牌的唯一性、有效期管理和安全性。
 */
@Service
@Slf4j
public class VerificationTokenServiceImpl implements VerificationTokenService {

    @Resource
    private VerificationTokenMapper verificationTokenMapper;

    @Resource
    private UserMapper userMapper;

    @Value("${app.email.verification-expiry:86400000}")  // 默认24小时
    private long emailVerificationExpiry;

    @Value("${app.email.reset-password-expiry:3600000}")  // 默认1小时
    private long passwordResetExpiry;

    @Override
    public String createEmailVerificationToken(Long userId) {
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 检查是否已经验证
        if (user.getActivated()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "邮箱已经验证");
        }

        // 使现有的邮箱验证令牌失效
        invalidateExistingTokens(userId, TokenType.EMAIL_VERIFICATION.getValue());

        // 创建新的令牌
        String token = generateUniqueToken();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUserId(userId);
        verificationToken.setTokenType(TokenType.EMAIL_VERIFICATION.getValue());
        verificationToken.setExpiresAt(new Date(System.currentTimeMillis() + emailVerificationExpiry));
        verificationToken.setCreatedAt(new Date());

        int result = verificationTokenMapper.insert(verificationToken);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建验证令牌失败");
        }

        log.info("创建邮箱验证令牌: userId={}, token={}", userId, token);
        return token;
    }

    @Override
    public String createPasswordResetToken(Long userId) {
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 检查是否为Google用户（Google用户不允许重置密码）
        if ("google".equals(user.getAuthType())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Google账号无法重置密码");
        }

        // 使现有的密码重置令牌失效
        invalidateExistingTokens(userId, TokenType.PASSWORD_RESET.getValue());

        // 创建新的令牌
        String token = generateUniqueToken();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUserId(userId);
        verificationToken.setTokenType(TokenType.PASSWORD_RESET.getValue());
        verificationToken.setExpiresAt(new Date(System.currentTimeMillis() + passwordResetExpiry));
        verificationToken.setCreatedAt(new Date());

        int result = verificationTokenMapper.insert(verificationToken);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建密码重置令牌失败");
        }

        log.info("创建密码重置令牌: userId={}, token={}", userId, token);
        return token;
    }

    @Override
    @Transactional
    public boolean verifyEmail(String token) {
        // 查找令牌
        QueryWrapper<VerificationToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("token", token)
                .eq("token_type", TokenType.EMAIL_VERIFICATION.getValue())
                .isNull("used_at");

        VerificationToken verificationToken = verificationTokenMapper.selectOne(queryWrapper);

        // 验证令牌是否存在
        if (verificationToken == null) {
            log.warn("邮箱验证失败：令牌不存在, token={}", token);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证链接无效");
        }

        // 验证令牌是否过期
        if (verificationToken.getExpiresAt().before(new Date())) {
            log.warn("邮箱验证失败：令牌已过期, token={}, userId={}", token, verificationToken.getUserId());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证链接已过期，请重新发送验证邮件");
        }

        // 获取用户
        User user = userMapper.selectById(verificationToken.getUserId());
        if (user == null) {
            log.error("邮箱验证失败：用户不存在, userId={}", verificationToken.getUserId());
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 检查是否已经验证
        if (user.getActivated()) {
            log.info("邮箱已经验证过: userId={}", user.getId());
            return true;
        }

        // 更新用户状态
        user.setActivated(true);
        user.setEmailVerifiedAt(new Date());
        int updateResult = userMapper.updateById(user);

        if (updateResult != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新用户状态失败");
        }

        // 标记令牌为已使用
        markTokenAsUsed(token);

        log.info("邮箱验证成功: userId={}, email={}", user.getId(), user.getEmail());
        return true;
    }

    @Override
    public Long validatePasswordResetToken(String token) {
        // 查找令牌
        QueryWrapper<VerificationToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("token", token)
                .eq("token_type", TokenType.PASSWORD_RESET.getValue())
                .isNull("used_at");

        VerificationToken verificationToken = verificationTokenMapper.selectOne(queryWrapper);

        // 验证令牌是否存在
        if (verificationToken == null) {
            log.warn("密码重置令牌验证失败：令牌不存在, token={}", token);
            return null;
        }

        // 验证令牌是否过期
        if (verificationToken.getExpiresAt().before(new Date())) {
            log.warn("密码重置令牌验证失败：令牌已过期, token={}, userId={}", token, verificationToken.getUserId());
            return null;
        }

        // 验证用户是否存在
        User user = userMapper.selectById(verificationToken.getUserId());
        if (user == null) {
            log.error("密码重置令牌验证失败：用户不存在, userId={}", verificationToken.getUserId());
            return null;
        }

        log.info("密码重置令牌验证成功: userId={}", user.getId());
        return user.getId();
    }

    @Override
    public void markTokenAsUsed(String token) {
        UpdateWrapper<VerificationToken> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("token", token)
                .set("used_at", new Date());

        int result = verificationTokenMapper.update(null, updateWrapper);
        if (result != 1) {
            log.warn("标记令牌为已使用失败: token={}", token);
        }
    }

    @Override
    public void cleanExpiredTokens() {
        // 删除过期超过7天的令牌
        Date sevenDaysAgo = new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L);

        QueryWrapper<VerificationToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.lt("expires_at", sevenDaysAgo);

        int deletedCount = verificationTokenMapper.delete(queryWrapper);
        log.info("清理过期令牌: 删除了 {} 条记录", deletedCount);
    }

    /**
     * 生成唯一的令牌
     * 使用UUID确保唯一性，并移除连字符使其更紧凑
     */
    private String generateUniqueToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 使指定用户的指定类型的现有令牌失效
     * 这确保了同一时间只有一个有效的令牌
     */
    private void invalidateExistingTokens(Long userId, String tokenType) {
        UpdateWrapper<VerificationToken> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId)
                .eq("token_type", tokenType)
                .isNull("used_at")
                .set("used_at", new Date());

        int updatedCount = verificationTokenMapper.update(null, updateWrapper);
        if (updatedCount > 0) {
            log.info("使 {} 个现有令牌失效: userId={}, tokenType={}", updatedCount, userId, tokenType);
        }
    }
}
