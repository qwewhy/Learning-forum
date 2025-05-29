
USE chemxnexus;
-- 检查 subscription 表的索引
SHOW INDEX FROM subscription;

-- 检查 user 表的索引
SHOW INDEX FROM user;

-- 检查 subscription 表字段
DESCRIBE subscription;

-- 检查 user 表字段
DESCRIBE user;

-- 查看是否有外键约束
SELECT
    TABLE_NAME,
    COLUMN_NAME,
    CONSTRAINT_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'chemxnexus'
  AND (TABLE_NAME = 'subscription' OR TABLE_NAME = 'user')
  AND REFERENCED_TABLE_NAME IS NOT NULL;

-- 检查 subscription 和 user 表中的 stripeCustomerId 是否一致
SELECT
    s.id as subscription_id,
    s.userId,
    s.stripeCustomerId as sub_stripe_customer,
    u.stripeCustomerId as user_stripe_customer,
    CASE
        WHEN s.stripeCustomerId = u.stripeCustomerId THEN 'MATCH'
        WHEN s.stripeCustomerId IS NULL OR u.stripeCustomerId IS NULL THEN 'NULL_VALUE'
        ELSE 'MISMATCH'
        END as stripe_customer_match
FROM subscription s
         INNER JOIN user u ON s.userId = u.id;