package com.HongyuanWang.learningforum.controller;

import com.HongyuanWang.learningforum.common.BaseResponse;
import com.HongyuanWang.learningforum.common.ErrorCode;
import com.HongyuanWang.learningforum.common.ResultUtils;
import com.HongyuanWang.learningforum.config.StripeConfig;
import com.HongyuanWang.learningforum.exception.BusinessException;
import com.HongyuanWang.learningforum.exception.ThrowUtils;
import com.HongyuanWang.learningforum.service.SubscriptionService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Stripe Webhook 控制器
 *
 * @author Hongyuan Wang
 */
@RestController
@RequestMapping("/stripe/webhook")
@Slf4j
public class StripeWebhookController {

    @Resource
    private SubscriptionService subscriptionService;

    @Resource
    private StripeConfig stripeConfig;

    @PostMapping
    public BaseResponse<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader, HttpServletRequest request) {
        ThrowUtils.throwIf(sigHeader == null, ErrorCode.FORBIDDEN_ERROR, "Missing Stripe signature.");

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeConfig.getWebhookSecret());
        } catch (SignatureVerificationException e) {
            log.error("Stripe webhook signature verification failed", e);
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "Invalid Stripe signature.");
        } catch (Exception e) {
            log.error("Error constructing Stripe event", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Error processing Stripe webhook.");
        }

        log.info("Received Stripe Webhook event: id={}, type={}", event.getId(), event.getType());

        try {
            switch (event.getType()) {
                case "checkout.session.completed":
                    subscriptionService.handleCheckoutSessionCompleted(event);
                    break;
                case "customer.subscription.updated":
                    subscriptionService.handleSubscriptionUpdated(event);
                    break;
                case "customer.subscription.deleted":
                    subscriptionService.handleSubscriptionDeleted(event);
                    break;
                case "invoice.payment_succeeded":
                    subscriptionService.handleInvoicePaymentSucceeded(event);
                    break;
                case "invoice.payment_failed":
                    subscriptionService.handleInvoicePaymentFailed(event);
                    break;
                default:
                    log.info("Unhandled Stripe event type: {}", event.getType());
            }
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            log.error("Error processing webhook event {} - {}: {}",event.getType(), event.getId(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Webhook event processing failed.");
        }

        return ResultUtils.success("Webhook received");
    }
} 