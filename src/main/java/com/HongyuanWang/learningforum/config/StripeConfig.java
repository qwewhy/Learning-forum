package com.HongyuanWang.learningforum.config;

import com.stripe.Stripe;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = "stripe")
@Data
public class StripeConfig {
    private String secretKey;
    private String publishableKey;
    private String webhookSecret;
    private String basicPriceId;
    private String premiumPriceId;
    private String enterprisePriceId;
    private String appBaseUrl;
    private String customerPortalReturnUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }
} 