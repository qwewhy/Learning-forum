package com.HongyuanWang.learningforum.annotation;

import com.HongyuanWang.learningforum.model.enums.SubscriptionPlan;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 要求订阅注解
 *
 * @author Hongyuan Wang
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireSubscription {

    /**
     * 要求的最低订阅计划
     *
     * @return
     */
    SubscriptionPlan value() default SubscriptionPlan.BASIC;

    /**
     * 是否必须，默认为 true。如果为 false，则即使没有订阅或订阅级别不够，也允许访问（例如用于提示升级）。
     * 但通常此注解用于强制要求订阅。
     * @return
     */
    boolean required() default true;
} 