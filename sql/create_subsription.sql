-- 创建 subscription 表
USE chemxnexus;
CREATE TABLE `subscription` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `stripe_customer_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Stripe Customer ID',
  `stripe_subscription_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Stripe Subscription ID',
  `stripe_price_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Stripe Price ID',
  `plan` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订阅计划 (basic, premium, enterprise)',
  `status` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订阅状态 (active, canceled, past_due, unpaid, trialing)',
  `current_period_start` timestamp NULL DEFAULT NULL COMMENT '当前计费周期开始时间',
  `current_period_end` timestamp NULL DEFAULT NULL COMMENT '当前计费周期结束时间',
  `cancel_at_period_end` tinyint(1) DEFAULT '0' COMMENT '是否在周期末取消',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_stripe_subscription_id` (`stripe_subscription_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_stripe_customer_id` (`stripe_customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户订阅表';

-- 在 user 表中添加 stripe_customer_id 字段
ALTER TABLE `user`
ADD COLUMN `stripe_customer_id` VARCHAR(255) NULL DEFAULT NULL COMMENT 'Stripe Customer ID' AFTER `vipNumber`,
ADD INDEX `idx_stripe_customer_id` (`stripe_customer_id`);


