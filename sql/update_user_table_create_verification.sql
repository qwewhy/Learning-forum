USE chemxnexus;

-- 针对 user 表的操作
-- 1. 先删除依赖于旧列名 google_id 的索引
-- 注意：如果索引 idx_google_id 不存在，此语句会报错。这是可接受的，因为目标是确保最终状态正确。
ALTER TABLE `user` DROP INDEX `idx_google_id`;

-- 2. 修改 user 表中的下划线命名字段为驼峰命名
ALTER TABLE `user`
    CHANGE COLUMN `google_id` `googleId` VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Google OAuth ID',
    CHANGE COLUMN `email_verified_at` `emailVerifiedAt` DATETIME DEFAULT NULL COMMENT '邮箱验证时间',
    CHANGE COLUMN `auth_type` `authType` VARCHAR(20) COLLATE utf8mb4_unicode_ci DEFAULT 'password' COMMENT '认证类型：password/google';

-- 3. 为新的 googleId 字段添加索引
-- 此操作前应确保 googleId 列已通过上面的 CHANGE COLUMN 操作成功创建/重命名
ALTER TABLE `user` ADD INDEX `idx_googleId` (`googleId`);

-- 关于 activated 字段：根据您提供的表结构，activated 字段已存在且格式正确，此处不再进行操作。
-- 关于 email 字段及其索引 idx_email：根据您提供的表结构，它们也已存在且格式正确，此处不再进行操作。


-- 针对 verification_token 表的操作
-- 如果 verification_token 表已存在，尝试将其下划线字段重命名为驼峰字段
-- 注意：如果表不存在，或者字段已是驼峰名，或者旧的下划线字段不存在，这些 CHANGE COLUMN 语句可能会报错，
-- 这通常是可以接受的，因为后续的 CREATE TABLE IF NOT EXISTS 会处理表和字段的最终状态。
ALTER TABLE `verification_token`
    CHANGE COLUMN `user_id` `userId` BIGINT NOT NULL COMMENT '用户ID',
    CHANGE COLUMN `token_type` `tokenType` VARCHAR(50) NOT NULL COMMENT '令牌类型：EMAIL_VERIFICATION或PASSWORD_RESET',
    CHANGE COLUMN `expires_at` `expiresAt` DATETIME NOT NULL COMMENT '过期时间',
    CHANGE COLUMN `created_at` `createdAt` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CHANGE COLUMN `used_at` `usedAt` DATETIME DEFAULT NULL COMMENT '使用时间';
-- 如果 id 和 token 字段也可能是下划线 (e.g., token_value)，也需要添加类似的 CHANGE COLUMN 语句。
-- 假设 id 和 token 字段名已经是正确的。

-- 创建 verification_token 表 (表名使用下划线，字段名使用驼峰)
-- 如果上面的 CHANGE COLUMN 因为表不存在而失败，此语句会创建表。
-- 如果表已存在且字段已通过上面的语句修改为驼峰，此语句不会执行任何操作。
CREATE TABLE IF NOT EXISTS `verification_token` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `token` VARCHAR(255) NOT NULL UNIQUE,
    `userId` BIGINT NOT NULL,
    `tokenType` VARCHAR(50) NOT NULL COMMENT 'EMAIL_VERIFICATION或PASSWORD_RESET',
    `expiresAt` DATETIME NOT NULL,
    `createdAt` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `usedAt` DATETIME,
    INDEX `idx_verification_token_token` (`token`),
    INDEX `idx_verification_token_userId` (`userId`),
    FOREIGN KEY (`userId`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='认证令牌表';