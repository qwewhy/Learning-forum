package com.HongyuanWang.learningforum.model.entity;

import com.HongyuanWang.learningforum.model.enums.SubscriptionPlan;
import com.HongyuanWang.learningforum.model.enums.SubscriptionStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 订阅实体
 *
 * @author Hongyuan Wang
 */
@TableName(value = "subscription")
@Data
public class Subscription implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID (外键关联到 User 表的 id)
     */
    private Long userId;

    /**
     * Stripe Customer ID
     */
    @TableField(value = "stripe_customer_id")
    private String stripeCustomerId;

    /**
     * Stripe Subscription ID
     */
    @TableField(value = "stripe_subscription_id")
    private String stripeSubscriptionId;

    /**
     * Stripe Price ID
     */
    @TableField(value = "stripe_price_id")
    private String stripePriceId;

    /**
     * 订阅状态
     */
    @TableField(value = "status")
    private String status; // 存储 SubscriptionStatus 的 value

    /**
     * 订阅计划
     */
    @TableField(value = "plan")
    private String plan; // 存储 SubscriptionPlan 的 value

    /**
     * 当前计费周期开始时间
     */
    @TableField(value = "current_period_start")
    private Date currentPeriodStart;

    /**
     * 当前计费周期结束时间
     */
    @TableField(value = "current_period_end")
    private Date currentPeriodEnd;

    /**
     * 是否在周期末取消
     */
    @TableField(value = "cancel_at_period_end")
    private Boolean cancelAtPeriodEnd;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
} 