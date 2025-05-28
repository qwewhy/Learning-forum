package com.HongyuanWang.learningforum.service.impl;

import com.HongyuanWang.learningforum.common.ErrorCode;
import com.HongyuanWang.learningforum.config.StripeConfig;
import com.HongyuanWang.learningforum.exception.BusinessException;
import com.HongyuanWang.learningforum.exception.ThrowUtils;
import com.HongyuanWang.learningforum.mapper.SubscriptionMapper;
import com.HongyuanWang.learningforum.mapper.UserMapper;
import com.HongyuanWang.learningforum.model.entity.Subscription;
import com.HongyuanWang.learningforum.model.entity.User;
import com.HongyuanWang.learningforum.model.enums.SubscriptionPlan;
import com.HongyuanWang.learningforum.model.enums.SubscriptionStatus;
import com.HongyuanWang.learningforum.service.SubscriptionService;
import com.HongyuanWang.learningforum.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.Invoice;
import com.stripe.model.StripeObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 订阅服务实现类
 *
 * @author Hongyuan Wang
 */
@Service
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    @Resource
    private StripeConfig stripeConfig;

    @Resource
    private SubscriptionMapper subscriptionMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    @Lazy
    private UserService userService;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeConfig.getSecretKey();
    }

    @Override
    public Subscription getActiveSubscriptionByUserId(Long userId) {
        QueryWrapper<Subscription> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .and(qw -> qw.eq("status", SubscriptionStatus.ACTIVE.getValue())
                        .or().eq("status", SubscriptionStatus.TRIALING.getValue()))
                .orderByDesc("create_time")
                .last("LIMIT 1");
        return subscriptionMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional
    public com.stripe.model.checkout.Session createCheckoutSession(User user, String priceId) {
        String stripeCustomerId = user.getStripeCustomerId();
        if (stripeCustomerId == null || stripeCustomerId.isEmpty()) {
            try {
                Map<String, Object> customerParams = new HashMap<>();
                customerParams.put("email", user.getUserAccount());
                customerParams.put("name", user.getUserName());
                Map<String, String> metadata = new HashMap<>();
                metadata.put("app_user_id", user.getId().toString());
                customerParams.put("metadata", metadata);
                Customer customer = Customer.create(customerParams);
                stripeCustomerId = customer.getId();
                user.setStripeCustomerId(stripeCustomerId);
                boolean updateResult = userMapper.updateById(user) > 0;
                ThrowUtils.throwIf(!updateResult, ErrorCode.SYSTEM_ERROR, "更新用户Stripe Customer ID失败");
            } catch (StripeException e) {
                log.error("创建Stripe Customer失败: {}, User ID: {}", e.getMessage(), user.getId(), e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建支付用户失败，请稍后重试");
            }
        }

        // 构建回调 URL
        String configuredSuccessUrl = stripeConfig.getAppBaseUrl() + "/subscription/success"; // 您可以根据需要调整路径
        String configuredCancelUrl = stripeConfig.getAppBaseUrl() + "/subscription/cancel"; // 您可以根据需要调整路径


        com.stripe.param.checkout.SessionCreateParams.LineItem lineItem =
            com.stripe.param.checkout.SessionCreateParams.LineItem.builder()
                .setPrice(priceId) // 直接使用 priceId
                .setQuantity(1L)
                .build();

        com.stripe.param.checkout.SessionCreateParams params =
            com.stripe.param.checkout.SessionCreateParams.builder()
                .setMode(com.stripe.param.checkout.SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(configuredSuccessUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(configuredCancelUrl)
                .addLineItem(lineItem)
                .setCustomer(stripeCustomerId)
                .putMetadata("app_user_id", user.getId().toString())
                .putMetadata("price_id", priceId) // 仍然需要 price_id 用于 webhook 处理
                .build();
        try {
            return com.stripe.model.checkout.Session.create(params);
        } catch (StripeException e) {
            log.error("创建Stripe Checkout Session失败: {}, User ID: {}", e.getMessage(), user.getId(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建支付会话失败，请稍后重试");
        }
    }

    @Override
    public com.stripe.model.billingportal.Session createCustomerPortalSession(User user) {
        ThrowUtils.throwIf(user.getStripeCustomerId() == null || user.getStripeCustomerId().isEmpty(), ErrorCode.OPERATION_ERROR, "用户尚未关联Stripe账户，无法打开管理门户。");
        
        // 使用配置的 returnUrl
        String configuredReturnUrl = stripeConfig.getAppBaseUrl() + stripeConfig.getCustomerPortalReturnUrl();

        com.stripe.param.billingportal.SessionCreateParams params =
            com.stripe.param.billingportal.SessionCreateParams.builder()
                .setCustomer(user.getStripeCustomerId())
                .setReturnUrl(configuredReturnUrl)
                .build();
        try {
            return com.stripe.model.billingportal.Session.create(params);
        } catch (StripeException e) {
            log.error("创建Stripe Customer Portal Session失败: {}, User ID: {}", e.getMessage(), user.getId(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "打开订阅管理门户失败，请稍后重试");
        }
    }

    @Override
    @Transactional
    public void handleCheckoutSessionCompleted(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = dataObjectDeserializer.getObject().orElse(null);
        ThrowUtils.throwIf(stripeObject == null, ErrorCode.SYSTEM_ERROR, "Webhook event data object is not present for event: " + event.getId());

        if (stripeObject instanceof com.stripe.model.checkout.Session) {
            com.stripe.model.checkout.Session session = (com.stripe.model.checkout.Session) stripeObject;
            log.info("处理 checkout.session.completed 事件, Session ID: {}", session.getId());

            String appUserIdStr = session.getMetadata().get("app_user_id");
            String priceId = session.getMetadata().get("price_id");

            ThrowUtils.throwIf(appUserIdStr == null, ErrorCode.SYSTEM_ERROR, "Webhook checkout.session.completed: app_user_id not found. Session ID: " + session.getId());
            Long appUserId;
            try {
                appUserId = Long.parseLong(appUserIdStr);
            } catch (NumberFormatException e) {
                 log.error("Webhook checkout.session.completed: Invalid app_user_id format {}. Session ID: {}", appUserIdStr, session.getId(), e);
                 throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Webhook处理错误：用户ID格式无效");
            }

            com.stripe.model.Subscription stripeSubscription;
            try {
                stripeSubscription = com.stripe.model.Subscription.retrieve(session.getSubscription());
            } catch (StripeException e) {
                log.error("Webhook checkout.session.completed: 无法从Stripe获取订阅详情. Stripe Subscription ID: {}, Error: {}", session.getSubscription(), e.getMessage(), e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Webhook处理错误：获取订阅详情失败");
            }

            Subscription localSubscription = findByStripeSubscriptionId(stripeSubscription.getId());
            if (localSubscription == null) {
                localSubscription = new Subscription();
                localSubscription.setUserId(appUserId);
                localSubscription.setStripeCustomerId(stripeSubscription.getCustomer());
                localSubscription.setStripeSubscriptionId(stripeSubscription.getId());
            }

            localSubscription.setStripePriceId(priceId);
            localSubscription.setPlan(getPlanValueByPriceId(priceId));
            localSubscription.setStatus(stripeSubscription.getStatus());
            localSubscription.setCurrentPeriodStart(new Date(stripeSubscription.getCurrentPeriodStart() * 1000L));
            localSubscription.setCurrentPeriodEnd(new Date(stripeSubscription.getCurrentPeriodEnd() * 1000L));
            localSubscription.setCancelAtPeriodEnd(stripeSubscription.getCancelAtPeriodEnd() != null && stripeSubscription.getCancelAtPeriodEnd());

            boolean saved = saveOrUpdateSubscription(localSubscription);
            ThrowUtils.throwIf(!saved, ErrorCode.SYSTEM_ERROR, "保存订阅信息失败 (checkout.session.completed)");
            log.info("用户 {} 的订阅 {} 已成功创建/更新 (checkout.session.completed)", appUserId, stripeSubscription.getId());

        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Webhook 收到的事件不是 Checkout Session 类型 (checkout.session.completed): " + event.getType());
        }
    }

    @Override
    @Transactional
    public void handleSubscriptionUpdated(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = dataObjectDeserializer.getObject().orElse(null);
        ThrowUtils.throwIf(stripeObject == null, ErrorCode.SYSTEM_ERROR, "Webhook event data object is not present for event: " + event.getId());

        if (stripeObject instanceof com.stripe.model.Subscription) {
            com.stripe.model.Subscription stripeSubscription = (com.stripe.model.Subscription) stripeObject;
            log.info("处理 customer.subscription.updated 事件, Stripe Subscription ID: {}", stripeSubscription.getId());

            Subscription localSubscription = findByStripeSubscriptionId(stripeSubscription.getId());
            if (localSubscription == null) {
                log.error("Webhook customer.subscription.updated: 未找到本地订阅记录. Stripe Subscription ID: {}. 尝试自动修复或创建.", stripeSubscription.getId());
                User user = userService.getUserByStripeCustomerId(stripeSubscription.getCustomer());
                ThrowUtils.throwIf(user == null, ErrorCode.SYSTEM_ERROR, "Webhook customer.subscription.updated: 无法通过Stripe Customer ID " + stripeSubscription.getCustomer() + " 找到用户。");
                
                localSubscription = new Subscription();
                localSubscription.setUserId(user.getId());
                localSubscription.setStripeCustomerId(stripeSubscription.getCustomer());
                localSubscription.setStripeSubscriptionId(stripeSubscription.getId());
            }

            String priceId = stripeSubscription.getItems().getData().get(0).getPrice().getId();
            localSubscription.setStripePriceId(priceId);
            localSubscription.setPlan(getPlanValueByPriceId(priceId));
            localSubscription.setStatus(stripeSubscription.getStatus());
            localSubscription.setCurrentPeriodStart(new Date(stripeSubscription.getCurrentPeriodStart() * 1000L));
            localSubscription.setCurrentPeriodEnd(new Date(stripeSubscription.getCurrentPeriodEnd() * 1000L));
            localSubscription.setCancelAtPeriodEnd(stripeSubscription.getCancelAtPeriodEnd() != null && stripeSubscription.getCancelAtPeriodEnd());

            boolean saved = saveOrUpdateSubscription(localSubscription);
            ThrowUtils.throwIf(!saved, ErrorCode.SYSTEM_ERROR, "更新订阅信息失败 (customer.subscription.updated)");
            log.info("用户 {} 的订阅 {} 已成功更新 (customer.subscription.updated)", localSubscription.getUserId(), stripeSubscription.getId());
        } else {
             throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Webhook 收到的事件不是 Subscription 类型 (customer.subscription.updated): " + event.getType());
        }
    }

    @Override
    @Transactional
    public void handleSubscriptionDeleted(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = dataObjectDeserializer.getObject().orElse(null);
        ThrowUtils.throwIf(stripeObject == null, ErrorCode.SYSTEM_ERROR, "Webhook event data object is not present for event: " + event.getId());

        if (stripeObject instanceof com.stripe.model.Subscription) {
            com.stripe.model.Subscription stripeSubscription = (com.stripe.model.Subscription) stripeObject;
            log.info("处理 customer.subscription.deleted 事件, Stripe Subscription ID: {}", stripeSubscription.getId());

            Subscription localSubscription = findByStripeSubscriptionId(stripeSubscription.getId());
            if (localSubscription != null) {
                localSubscription.setStatus(SubscriptionStatus.CANCELED.getValue());
                localSubscription.setCurrentPeriodEnd(new Date(stripeSubscription.getCurrentPeriodEnd() * 1000L));
                localSubscription.setCancelAtPeriodEnd(true);
                boolean saved = saveOrUpdateSubscription(localSubscription);
                ThrowUtils.throwIf(!saved, ErrorCode.SYSTEM_ERROR, "标记订阅为取消失败 (customer.subscription.deleted)");
                log.info("用户 {} 的订阅 {} 已被标记为取消 (customer.subscription.deleted)", localSubscription.getUserId(), stripeSubscription.getId());
            } else {
                log.error("Webhook customer.subscription.deleted: 未找到本地订阅记录. Stripe Subscription ID: {}.", stripeSubscription.getId());
            }
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Webhook 收到的事件不是 Subscription 类型 (customer.subscription.deleted): " + event.getType());
        }
    }

    @Override
    @Transactional
    public void handleInvoicePaymentSucceeded(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = dataObjectDeserializer.getObject().orElse(null);
        ThrowUtils.throwIf(stripeObject == null, ErrorCode.SYSTEM_ERROR, "Webhook event data object is not present for event: " + event.getId());

        if (stripeObject instanceof Invoice) {
            Invoice invoice = (Invoice) stripeObject;
            log.info("处理 invoice.payment_succeeded 事件, Invoice ID: {}, Subscription ID: {}", invoice.getId(), invoice.getSubscription());

            ThrowUtils.throwIf(invoice.getSubscription() == null, ErrorCode.OPERATION_ERROR, "Webhook invoice.payment_succeeded: Invoice " + invoice.getId() + " 不包含订阅信息，可能是单次支付。");
            
            com.stripe.model.Subscription stripeSubscription;
            try {
                stripeSubscription = com.stripe.model.Subscription.retrieve(invoice.getSubscription());
            } catch (StripeException e) {
                log.error("Webhook invoice.payment_succeeded: 无法从Stripe获取订阅详情. Stripe Subscription ID: {}, Error: {}", invoice.getSubscription(), e.getMessage(), e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Webhook处理错误：获取订阅详情失败");
            }

            Subscription localSubscription = findByStripeSubscriptionId(stripeSubscription.getId());
            if (localSubscription == null) {
                log.error("Webhook invoice.payment_succeeded: 未找到本地订阅记录. Stripe Subscription ID: {}. 尝试自动修复或创建.", stripeSubscription.getId());
                User user = userService.getUserByStripeCustomerId(stripeSubscription.getCustomer());
                ThrowUtils.throwIf(user == null, ErrorCode.SYSTEM_ERROR, "Webhook invoice.payment_succeeded: 无法通过Stripe Customer ID " + stripeSubscription.getCustomer() + " 找到用户。");

                localSubscription = new Subscription();
                localSubscription.setUserId(user.getId());
                localSubscription.setStripeCustomerId(stripeSubscription.getCustomer());
                localSubscription.setStripeSubscriptionId(stripeSubscription.getId());
            }

            String priceId = stripeSubscription.getItems().getData().get(0).getPrice().getId();
            localSubscription.setStripePriceId(priceId);
            localSubscription.setPlan(getPlanValueByPriceId(priceId));
            localSubscription.setStatus(stripeSubscription.getStatus());
            localSubscription.setCurrentPeriodStart(new Date(stripeSubscription.getCurrentPeriodStart() * 1000L));
            localSubscription.setCurrentPeriodEnd(new Date(stripeSubscription.getCurrentPeriodEnd() * 1000L));
            localSubscription.setCancelAtPeriodEnd(stripeSubscription.getCancelAtPeriodEnd() != null && stripeSubscription.getCancelAtPeriodEnd());

            boolean saved = saveOrUpdateSubscription(localSubscription);
            ThrowUtils.throwIf(!saved, ErrorCode.SYSTEM_ERROR, "更新订阅信息失败 (invoice.payment_succeeded)");
            log.info("用户 {} 的订阅 {} 已成功续费/更新 (invoice.payment_succeeded)", localSubscription.getUserId(), stripeSubscription.getId());
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Webhook 收到的事件不是 Invoice 类型 (invoice.payment_succeeded): " + event.getType());
        }
    }

    @Override
    @Transactional
    public void handleInvoicePaymentFailed(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = dataObjectDeserializer.getObject().orElse(null);
        ThrowUtils.throwIf(stripeObject == null, ErrorCode.SYSTEM_ERROR, "Webhook event data object is not present for event: " + event.getId());

        if (stripeObject instanceof Invoice) {
            Invoice invoice = (Invoice) stripeObject;
            log.info("处理 invoice.payment_failed 事件, Invoice ID: {}, Subscription ID: {}", invoice.getId(), invoice.getSubscription());

            ThrowUtils.throwIf(invoice.getSubscription() == null, ErrorCode.OPERATION_ERROR, "Webhook invoice.payment_failed: Invoice " + invoice.getId() + " 不包含订阅信息。");

            com.stripe.model.Subscription stripeSubscription;
            try {
                stripeSubscription = com.stripe.model.Subscription.retrieve(invoice.getSubscription());
            } catch (StripeException e) {
                log.error("Webhook invoice.payment_failed: 无法从Stripe获取订阅详情. Stripe Subscription ID: {}, Error: {}", invoice.getSubscription(), e.getMessage(), e);
                Subscription localSub = findByStripeSubscriptionId(invoice.getSubscription());
                if (localSub != null) {
                    localSub.setStatus(SubscriptionStatus.PAST_DUE.getValue()); 
                    log.info("用户 {} 的订阅 {} 因支付失败且无法获取Stripe最新状态，本地状态已尝试更新为逾期 (invoice.payment_failed)", localSub.getUserId(), localSub.getStripeSubscriptionId());
                    // 即使本地更新成功，原始的Stripe API错误也应导致处理失败
                    // saveOrUpdateSubscription(localSub); 
                }
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Webhook处理错误：无法从Stripe获取订阅详情以处理invoice.payment_failed事件。Error: " + e.getMessage());
            }
            
            Subscription localSubscription = findByStripeSubscriptionId(stripeSubscription.getId());
            if (localSubscription != null) {
                localSubscription.setStatus(stripeSubscription.getStatus()); // 使用从Stripe获取的最新状态
                localSubscription.setCurrentPeriodStart(new Date(stripeSubscription.getCurrentPeriodStart() * 1000L));
                localSubscription.setCurrentPeriodEnd(new Date(stripeSubscription.getCurrentPeriodEnd() * 1000L));
                boolean saved = saveOrUpdateSubscription(localSubscription);
                ThrowUtils.throwIf(!saved, ErrorCode.SYSTEM_ERROR, "更新订阅状态失败 (invoice.payment_failed)");
                log.info("用户 {} 的订阅 {} 因支付失败，状态已更新为 {} (invoice.payment_failed)", localSubscription.getUserId(), stripeSubscription.getId(), stripeSubscription.getStatus());
            } else {
                 log.error("Webhook invoice.payment_failed: 未找到本地订阅记录. Stripe Subscription ID: {}.", stripeSubscription.getId());
                 // 即使本地没有记录，也应该尝试基于Stripe的数据创建一个，因为这是一个有效的Stripe事件
                 // 但前提是能从Stripe Customer ID 找到 User
                 User user = userService.getUserByStripeCustomerId(stripeSubscription.getCustomer());
                 if (user != null) {
                    localSubscription = new Subscription();
                    localSubscription.setUserId(user.getId());
                    localSubscription.setStripeCustomerId(stripeSubscription.getCustomer());
                    localSubscription.setStripeSubscriptionId(stripeSubscription.getId());
                    String priceId = stripeSubscription.getItems().getData().get(0).getPrice().getId();
                    localSubscription.setStripePriceId(priceId);
                    localSubscription.setPlan(getPlanValueByPriceId(priceId));
                    localSubscription.setStatus(stripeSubscription.getStatus());
                    localSubscription.setCurrentPeriodStart(new Date(stripeSubscription.getCurrentPeriodStart() * 1000L));
                    localSubscription.setCurrentPeriodEnd(new Date(stripeSubscription.getCurrentPeriodEnd() * 1000L));
                    localSubscription.setCancelAtPeriodEnd(stripeSubscription.getCancelAtPeriodEnd() != null && stripeSubscription.getCancelAtPeriodEnd());
                    boolean saved = saveOrUpdateSubscription(localSubscription);
                    ThrowUtils.throwIf(!saved, ErrorCode.SYSTEM_ERROR, "创建新订阅记录失败 (invoice.payment_failed)");
                    log.info("Webhook invoice.payment_failed: 为用户 {} 创建了新的订阅记录 {}，状态为 {}。", user.getId(), stripeSubscription.getId(), stripeSubscription.getStatus());
                 } else {
                    log.error("Webhook invoice.payment_failed: 未找到本地订阅记录，也无法通过Stripe Customer ID {} 找到用户，无法处理Stripe Subscription ID: {}.", stripeSubscription.getCustomer(), stripeSubscription.getId());
                    // 这种情况也应该抛出异常，因为我们无法将Stripe事件与系统内的用户关联
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Webhook处理错误：invoice.payment_failed 事件中的Stripe Customer ID 未关联到任何用户。");
                 }
            }
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Webhook 收到的事件不是 Invoice 类型 (invoice.payment_failed): " + event.getType());
        }
    }

    private String getPlanValueByPriceId(String priceId) {
        ThrowUtils.throwIf(priceId == null, ErrorCode.PARAMS_ERROR, "Price ID 不能为空");
        if (priceId.equals(stripeConfig.getBasicPriceId())) return SubscriptionPlan.BASIC.getValue();
        if (priceId.equals(stripeConfig.getPremiumPriceId())) return SubscriptionPlan.PREMIUM.getValue();
        if (priceId.equals(stripeConfig.getEnterprisePriceId())) return SubscriptionPlan.ENTERPRISE.getValue();
        log.error("未知的 Price ID: {}, 无法映射到订阅计划", priceId);
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "未知的订阅计划ID: " + priceId);
    }

    @Override
    public Subscription findByStripeSubscriptionId(String stripeSubscriptionId) {
        ThrowUtils.throwIf(stripeSubscriptionId == null || stripeSubscriptionId.isEmpty(), ErrorCode.PARAMS_ERROR, "Stripe Subscription ID 不能为空");
        QueryWrapper<Subscription> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("stripe_subscription_id", stripeSubscriptionId);
        return subscriptionMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional
    public boolean saveOrUpdateSubscription(Subscription subscription) {
        ThrowUtils.throwIf(subscription == null, ErrorCode.PARAMS_ERROR, "订阅信息不能为空");
        if (subscription.getId() == null) {
            Subscription existing = findByStripeSubscriptionId(subscription.getStripeSubscriptionId());
            if (existing != null) {
                subscription.setId(existing.getId());
                subscription.setCreateTime(existing.getCreateTime());
                subscription.setUpdateTime(new Date());
                return subscriptionMapper.updateById(subscription) > 0;
            }
            subscription.setCreateTime(new Date());
            subscription.setUpdateTime(new Date());
            return subscriptionMapper.insert(subscription) > 0;
        } else {
            subscription.setUpdateTime(new Date());
            return subscriptionMapper.updateById(subscription) > 0;
        }
    }
} 