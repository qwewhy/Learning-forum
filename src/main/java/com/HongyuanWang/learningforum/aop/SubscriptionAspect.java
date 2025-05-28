package com.HongyuanWang.learningforum.aop;

import com.HongyuanWang.learningforum.annotation.RequireSubscription;
import com.HongyuanWang.learningforum.common.ErrorCode;
import com.HongyuanWang.learningforum.exception.BusinessException;
import com.HongyuanWang.learningforum.model.entity.User;
import com.HongyuanWang.learningforum.model.entity.Subscription;
import com.HongyuanWang.learningforum.model.enums.SubscriptionPlan;
import com.HongyuanWang.learningforum.model.enums.SubscriptionStatus;
import com.HongyuanWang.learningforum.service.UserService;
import com.HongyuanWang.learningforum.service.SubscriptionService; // 假设有这个服务
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订阅权限校验 AOP
 *
 * @author Hongyuan Wang
 */
@Aspect
@Component
public class SubscriptionAspect {

    @Resource
    @Lazy
    private UserService userService;

    @Resource
    @Lazy
    private SubscriptionService subscriptionService; // 注入订阅服务

    /**
     * 定义切点，拦截带有 @RequireSubscription 注解的方法或类
     */
    @Around("@within(requireSubscription) || @annotation(requireSubscription)")
    public Object checkSubscription(ProceedingJoinPoint joinPoint, RequireSubscription requireSubscription) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "无法获取请求属性");
        }
        HttpServletRequest request = attributes.getRequest();

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 获取方法或类上的注解实例
        RequireSubscription annotation = getAnnotation(joinPoint, requireSubscription);
        if (annotation == null || !annotation.required()) {
            return joinPoint.proceed(); // 如果注解不存在或非必须，则直接放行
        }

        SubscriptionPlan requiredPlan = annotation.value();

        // 查询用户当前的有效订阅
        Subscription currentSubscription = subscriptionService.getActiveSubscriptionByUserId(loginUser.getId());

        if (currentSubscription == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "您需要订阅才能访问此功能。当前未订阅。");
        }

        // 校验订阅状态是否有效
        if (!SubscriptionStatus.ACTIVE.getValue().equals(currentSubscription.getStatus()) &&
            !SubscriptionStatus.TRIALING.getValue().equals(currentSubscription.getStatus())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "您的订阅当前状态无效 (" + currentSubscription.getStatus() + ")，请检查您的订阅。");
        }

        // 校验订阅计划级别
        SubscriptionPlan userPlan = SubscriptionPlan.getEnumByValue(currentSubscription.getPlan());
        if (userPlan == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "无法识别您的订阅计划类型。");
        }

        // 定义计划的层级，数字越大，级别越高
        List<SubscriptionPlan> planHierarchy = Arrays.asList(SubscriptionPlan.BASIC, SubscriptionPlan.PREMIUM, SubscriptionPlan.ENTERPRISE);

        if (planHierarchy.indexOf(userPlan) < planHierarchy.indexOf(requiredPlan)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, 
                String.format("您的订阅计划 (%s) 不足以访问此功能。需要 %s 或更高级别的计划。", 
                              userPlan.getDescription(), requiredPlan.getDescription()));
        }

        // 权限校验通过，执行原方法
        return joinPoint.proceed();
    }

    private RequireSubscription getAnnotation(ProceedingJoinPoint joinPoint, RequireSubscription classAnnotation) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequireSubscription methodAnnotation = method.getAnnotation(RequireSubscription.class);
        // 方法注解优先于类注解
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        return classAnnotation; // 如果方法上没有，则使用类上的（通过 @within 传入的）
    }
} 