package com.HongyuanWang.learningforum.job.cycle;

import com.HongyuanWang.learningforum.service.VerificationTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 令牌清理定时任务
 * 定期清理过期的验证令牌，保持数据库整洁
 */
@Component
@Slf4j
public class TokenCleanupScheduler {

    @Resource
    private VerificationTokenService verificationTokenService;

    /**
     * 每天凌晨2点执行清理任务
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredTokens() {
        log.info("开始执行令牌清理任务");
        try {
            verificationTokenService.cleanExpiredTokens();
            log.info("令牌清理任务执行完成");
        } catch (Exception e) {
            log.error("令牌清理任务执行失败", e);
        }
    }
}