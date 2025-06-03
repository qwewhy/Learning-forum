package com.HongyuanWang.learningforum.service.impl;

import com.HongyuanWang.learningforum.common.ErrorCode;
import com.HongyuanWang.learningforum.exception.BusinessException;
import com.HongyuanWang.learningforum.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private TemplateEngine templateEngine;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.email.from}")
    private String fromEmail;

    @Override
    public void sendVerificationEmail(String to, String token, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("请验证您的邮箱 - 学习刷题论坛");

            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("verifyUrl", frontendUrl + "/verify-email?token=" + token);
            // 假设您有一个名为 "email/verificationEmail" 的Thymeleaf模板
            String content = templateEngine.process("email/verificationEmail", context);

            helper.setText(content, true);
            mailSender.send(message);

            log.info("验证邮件已发送至: {}", to);
        } catch (MessagingException e) {
            log.error("发送验证邮件失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "发送邮件失败，请稍后重试");
        }
    }

    @Override
    public void sendPasswordResetEmail(String to, String token, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("密码重置请求 - 学习刷题论坛");

            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("resetUrl", frontendUrl + "/reset-password?token=" + token);
            // 假设您有一个名为 "email/passwordResetEmail" 的Thymeleaf模板
            String content = templateEngine.process("email/passwordResetEmail", context);
            
            helper.setText(content, true);
            mailSender.send(message);

            log.info("密码重置邮件已发送至: {}", to);
        } catch (MessagingException e) {
            log.error("发送密码重置邮件失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "发送邮件失败，请稍后重试");
        }
    }

    @Override
    public void sendWelcomeEmail(String to, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("欢迎加入学习刷题论坛！");

            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("loginUrl", frontendUrl + "/login"); // 或者您的登录页面URL
            // 假设您有一个名为 "email/welcomeEmail" 的Thymeleaf模板
            String content = templateEngine.process("email/welcomeEmail", context);

            helper.setText(content, true);
            mailSender.send(message);

            log.info("欢迎邮件已发送至: {}", to);
        } catch (MessagingException e) {
            log.error("发送欢迎邮件失败: {}", e.getMessage(), e);
            // 对于欢迎邮件，失败可能不需要抛出中断流程的异常，可以考虑仅记录日志或标记用户状态
            // throw new BusinessException(ErrorCode.SYSTEM_ERROR, "发送邮件失败，请稍后重试");
        }
    }
}
