package com.HongyuanWang.learningforum.model.enums;

/**
 * 订阅计划枚举
 */
public enum SubscriptionPlan {
    BASIC("basic", "基础版"),
    PREMIUM("premium", "高级版"),
    ENTERPRISE("enterprise", "企业版");

    private final String value;
    private final String description;

    SubscriptionPlan(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static SubscriptionPlan getEnumByValue(String value) {
        if (value == null) {
            return null;
        }
        for (SubscriptionPlan plan : SubscriptionPlan.values()) {
            if (plan.getValue().equals(value)) {
                return plan;
            }
        }
        return null;
    }
} 