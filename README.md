# SpringBoot 学习论坛项目文档

## 项目概述
**项目名称**：SpringBoot LearningForum  
**作者**：Hongyuan Wang  
**描述**：基于 Java SpringBoot 的项目初始模板，整合了常用框架和主流业务的示例代码。

## 技术栈与特性

### 主流框架 & 特性
- Spring Boot 2.7.x
- Spring MVC
- MyBatis + MyBatis Plus 数据访问（开启分页）
- Spring Boot 调试工具和项目处理器
- Spring AOP 切面编程
- Spring Scheduler 定时任务
- Spring 事务注解

### 数据存储
- MySQL 数据库
- Redis 内存数据库
- Elasticsearch 搜索引擎
- 腾讯云 COS 对象存储

### 工具类
- Easy Excel 表格处理
- Hutool 工具库
- Apache Commons Lang3 工具类
- Lombok 注解

### 业务特性
- 业务代码生成器（支持自动生成 Service、Controller、数据模型代码）
- Spring Session Redis 分布式登录
- 全局请求响应拦截器（记录日志）
- 全局异常处理器
- 自定义错误码
- 封装通用响应类
- Swagger + Knife4j 接口文档
- 自定义权限注解 + 全局校验
- 全局跨域处理
- 长整数丢失精度解决
- 多环境配置

### 业务功能
- 提供示例 SQL（用户、帖子、帖子点赞、帖子收藏表）
- 用户登录、注册、注销、更新、检索、权限管理
- 帖子创建、删除、编辑、更新、数据库检索、ES 灵活检索
- 帖子点赞、取消点赞
- 帖子收藏、取消收藏、检索已收藏帖子
- 帖子全量同步 ES、增量同步 ES 定时任务
- 支持微信开放平台登录
- 支持微信公众号订阅、收发消息、设置菜单
- 支持分业务的文件上传
- JUnit5 单元测试

## 快速上手指南
所有需要修改的地方 Hongyuan Wang 都标记了 todo，便于快速找到修改的位置

### MySQL 数据库配置
1. 修改 `application.yml` 的数据库配置为你自己的：
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/learning_forum
    username: root
    password: 123456
```

2. 执行 `sql/create_table.sql` 中的数据库语句，自动创建库表

3. 启动项目，访问 http://localhost:8101/api/doc.html 即可打开接口文档，不需要写前端就能在线调试接口

### Redis 分布式登录配置
1. 修改 `application.yml` 的 Redis 配置为自己的：
```yaml
spring:
  redis:
    database: 1
    host: localhost
    port: 6379
    timeout: 5000
    password: 123456
```

2. 修改 `application.yml` 中的 session 存储方式：
```yaml
spring:
  session:
    store-type: redis
```

3. 移除 `MainApplication` 类开头 `@SpringBootApplication` 注解内的 exclude 参数：

修改前：
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})


修改后：
@SpringBootApplication


### Elasticsearch 搜索引擎配置
1. 修改 `application.yml` 的 Elasticsearch 配置为自己的：
```yaml
spring:
  elasticsearch:
    uris: http://localhost:9200
    username: root
    password: 123456
```

2. 复制 `sql/post_es_mapping.json` 文件中的内容，通过调用 Elasticsearch 的接口或者 Kibana Dev Tools 来创建索引（相当于数据库建表）：
```
PUT post_v1
{
  参数见 sql/post_es_mapping.json 文件
}
```

3. 开启同步任务，将数据库的帖子同步到 Elasticsearch：找到 job 目录下的 `FullSyncPostToEs` 和 `IncSyncPostToEs` 文件，取消掉 `@Component` 注解的注释，再次执行程序即可触发同步：
```java
// todo 取消注释开启任务
//@Component
```

### 业务代码生成器使用
支持自动生成 Service、Controller、数据模型代码，配合 MyBatisX 插件，可以快速开发增删改查等实用基础功能。

找到 `generate.CodeGenerator` 类，修改生成参数和生成路径，并且支持注释掉不需要的生成逻辑，然后运行即可：
```java
// 指定生成参数
String packageName = "com.HongyuanWang.learningforum";
String dataName = "用户评论";
String dataKey = "userComment";
String upperDataKey = "UserComment";
```

生成代码后，可以移动到实际项目中，并且按照 // todo 注释的提示来针对自己的业务需求进行修改。

## 架构设计
- 合理分层
- 代码注释完善
- 快速上手便捷

### Stripe 订阅服务集成
本项目集成了 Stripe 用于处理付费订阅服务。允许通过注解控制对特定 API 的访问权限，区分不同订阅级别的用户。

#### 1. 配置 Stripe
在 `src/main/resources/application.yml` (或 `.properties`) 文件中配置您的 Stripe API 密钥和价格 ID：

```yaml
stripe:
  secretKey: sk_test_your_stripe_secret_key # 替换为您的Stripe Secret Key
  publishableKey: pk_test_your_stripe_publishable_key # 替换为您的Stripe Publishable Key
  webhookSecret: whsec_your_stripe_webhook_secret # 替换为您的Stripe Webhook Signing Secret
  basicPriceId: price_your_basic_plan_price_id # 基础版价格ID
  premiumPriceId: price_your_premium_plan_price_id # 高级版价格ID
  enterprisePriceId: price_your_enterprise_plan_price_id # 企业版价格ID
```

#### 2. 数据库迁移
执行以下 SQL 语句以创建 `subscription` 表并更新 `user` 表：

```sql
-- 创建 subscription 表
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
```

#### 3. Stripe Webhook 配置
在您的 Stripe Dashboard 中，配置一个 Webhook 端点，指向您部署的应用的 `/stripe/webhook` 路径 (例如 `https://yourdomain.com/api/stripe/webhook`)。

确保选择监听以下事件：
- `checkout.session.completed`
- `customer.subscription.updated`
- `customer.subscription.deleted`
- `invoice.payment_succeeded`
- `invoice.payment_failed`

将 Stripe 生成的 Webhook Signing Secret 配置到 `application.yml` 的 `stripe.webhookSecret` 中。

#### 4. 使用 `@RequireSubscription` 注解
通过 `@RequireSubscription` 注解可以控制对 Controller 方法或整个 Controller 类的访问权限。该注解可以指定所需的最低订阅级别。

**注解参数：**
- `value`: `SubscriptionPlan` 枚举类型，指定要求的最低订阅计划（默认为 `SubscriptionPlan.BASIC`）。可选值：`BASIC`, `PREMIUM`, `ENTERPRISE`。
- `required`: boolean 类型，指示订阅是否为强制性的（默认为 `true`）。

**示例：**

保护单个 Controller 方法，要求至少为高级版订阅：
```java
import com.HongyuanWang.learningforum.annotation.RequireSubscription;
import com.HongyuanWang.learningforum.model.enums.SubscriptionPlan;
// ...

@RestController
@RequestMapping("/api/premium-feature")
public class PremiumFeatureController {

    @GetMapping("/access")
    @RequireSubscription(SubscriptionPlan.PREMIUM)
    public BaseResponse<String> accessPremiumFeature() {
        return ResultUtils.success("成功访问高级功能!");
    }
}
```

保护整个 Controller 类，所有方法都要求至少为基础版订阅：
```java
import com.HongyuanWang.learningforum.annotation.RequireSubscription;
// ...

@RestController
@RequestMapping("/api/basic-feature")
@RequireSubscription // 默认为 BASIC
public class BasicFeatureController {

    @GetMapping("/info")
    public BaseResponse<String> getBasicInfo() {
        return ResultUtils.success("成功获取基础信息!");
    }
}
```

如果用户未登录、没有有效订阅或订阅级别不满足要求，访问受保护的 API 时将返回相应的错误信息。

#### 5. 核心组件
- **`StripeConfig.java`**: 配置 Stripe API 密钥和价格 ID。
- **`Subscription.java`**: 用户订阅信息的实体类。
- **`SubscriptionMapper.java`**: `Subscription` 实体的 MyBatis Plus Mapper。
- **`SubscriptionPlan.java`, `SubscriptionStatus.java`**: 定义订阅计划和状态的枚举。
- **`RequireSubscription.java`**: 权限控制注解。
- **`SubscriptionAspect.java`**: AOP 切面，实现基于 `@RequireSubscription` 的权限校验逻辑。
- **`SubscriptionService.java` / `SubscriptionServiceImpl.java`**: 处理订阅相关业务逻辑，包括创建 Stripe Checkout Session 和处理 Stripe Webhook 事件。
- **`StripeWebhookController.java`**:接收并验证来自 Stripe 的 Webhook 事件，并将其分发给 `SubscriptionService` 处理。
- **`User.java`**: 扩展了 `stripeCustomerId` 字段。
- **`UserService.java` / `UserServiceImpl.java`**: 扩展了通过 `stripeCustomerId` 查询用户的方法。