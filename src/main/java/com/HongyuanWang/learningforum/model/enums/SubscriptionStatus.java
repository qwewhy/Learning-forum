package com.HongyuanWang.learningforum.model.enums;

/**
 * 订阅状态枚举
 */
public enum SubscriptionStatus {
    ACTIVE("active", "活跃"),
    CANCELED("canceled", "已取消"),
    PAST_DUE("past_due", "逾期未付"),
    UNPAID("unpaid", "未支付"),
    TRIALING("trialing", "试用中");

    private final String value;
    private final String description;

    SubscriptionStatus(String value, String description) {
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
    public static SubscriptionStatus getEnumByValue(String value) {
        if (value == null) {
            return null;
        }
        for (SubscriptionStatus status : SubscriptionStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
} 