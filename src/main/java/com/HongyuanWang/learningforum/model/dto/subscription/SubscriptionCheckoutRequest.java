package com.HongyuanWang.learningforum.model.dto.subscription;

import lombok.Data;
import lombok.ToString;
import java.io.Serializable;

/**
 * 订阅结账请求
 *
 * @author Hongyuan Wang
 */
@Data
@ToString
public class SubscriptionCheckoutRequest implements Serializable {

    /**
     * Stripe Price ID
     */
    private String priceId;

    private static final long serialVersionUID = 1L;
} 