package com.HongyuanWang.learningforum.service;

import com.HongyuanWang.learningforum.model.entity.Subscription;
import com.HongyuanWang.learningforum.model.entity.User;
import com.stripe.model.Event;
// 使用 FQN 来避免导入冲突，或者选择一个并重命名另一个的导入
// import com.stripe.model.checkout.Session;
// import com.stripe.model.billingportal.Session;

/**
 * 订阅服务接口
 *
 * @author Hongyuan Wang
 */
public interface SubscriptionService {

    /**
     * 根据用户ID获取有效的订阅信息
     *
     * @param userId 用户ID
     * @return 订阅信息，如果不存在或无效则返回null
     */
    Subscription getActiveSubscriptionByUserId(Long userId);

    /**
     * 创建 Stripe Checkout Session
     *
     * @param user      用户实体
     * @param priceId   Stripe 价格ID
     * @return com.stripe.model.checkout.Session
     */
    com.stripe.model.checkout.Session createCheckoutSession(User user, String priceId);

    /**
     * 创建 Stripe Customer Portal Session (客户订阅管理门户)
     *
     * @param user 用户实体
     * @return com.stripe.model.billingportal.Session
     */
    com.stripe.model.billingportal.Session createCustomerPortalSession(User user);

    /**
     * 处理 Stripe Webhook 事件：checkout.session.completed
     * 当用户成功完成支付并创建订阅时触发
     *
     * @param event Stripe Event 对象
     */
    void handleCheckoutSessionCompleted(Event event);

    /**
     * 处理 Stripe Webhook 事件：customer.subscription.updated
     * 当订阅状态更新时触发（例如：续订、升级、降级、付款失败变为active等）
     *
     * @param event Stripe Event 对象
     */
    void handleSubscriptionUpdated(Event event);

    /**
     * 处理 Stripe Webhook 事件：customer.subscription.deleted
     * 当订阅被取消或结束时触发
     *
     * @param event Stripe Event 对象
     */
    void handleSubscriptionDeleted(Event event);

    /**
     * 处理 Stripe Webhook 事件：invoice.payment_succeeded
     * 当发票支付成功时触发（通常用于续订）
     *
     * @param event Stripe Event 对象
     */
    void handleInvoicePaymentSucceeded(Event event);

    /**
     * 处理 Stripe Webhook 事件：invoice.payment_failed
     * 当发票支付失败时触发
     *
     * @param event Stripe Event 对象
     */
    void handleInvoicePaymentFailed(Event event);

    /**
     * 处理 Stripe Webhook 事件：invoice_payment.paid
     * 当发票支付成功时（通过 InvoicePayment 对象）触发
     *
     * @param event Stripe Event 对象
     */
    void handleInvoicePaymentPaid(Event event);

    /**
     * 根据 Stripe Subscription ID 查找本地订阅记录
     * @param stripeSubscriptionId Stripe 订阅ID
     * @return 本地订阅实体
     */
    Subscription findByStripeSubscriptionId(String stripeSubscriptionId);

    /**
     * 保存或更新订阅信息
     * @param subscription 订阅实体
     * @return 是否成功
     */
    boolean saveOrUpdateSubscription(Subscription subscription);

    /**
     * 验证用户是否具备订阅条件（主要是邮箱验证）
     * @param user 用户实体
     * @return 是否具备订阅条件
     */
    boolean validateUserForSubscription(User user);
} 