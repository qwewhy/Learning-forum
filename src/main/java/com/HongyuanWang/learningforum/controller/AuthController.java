package com.HongyuanWang.learningforum.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.HongyuanWang.learningforum.common.BaseResponse;
import com.HongyuanWang.learningforum.common.ErrorCode;
import com.HongyuanWang.learningforum.common.ResultUtils;
import com.HongyuanWang.learningforum.exception.BusinessException;
import com.HongyuanWang.learningforum.exception.ThrowUtils;
import com.HongyuanWang.learningforum.handler.OAuth2LoginSuccessHandler;
import com.HongyuanWang.learningforum.model.dto.auth.*;
import com.HongyuanWang.learningforum.model.entity.User;
import com.HongyuanWang.learningforum.model.vo.LoginUserVO;
import com.HongyuanWang.learningforum.service.UserService;
import com.HongyuanWang.learningforum.service.VerificationTokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.HongyuanWang.learningforum.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 认证控制器 - 处理所有认证相关的请求
 * 包括：邮箱注册、邮箱登录、Google OAuth登录、密码重置等
 *
 * @author Hongyuan Wang
 */
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Resource
    private UserService userService;

    @Resource
    private VerificationTokenService verificationTokenService;

    @Resource
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    /**
     * 邮箱注册
     * 用户提供邮箱、用户名和密码进行注册，系统会发送验证邮件
     */
    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody EmailRegisterRequest request) {
        // 参数校验
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        String email = request.getEmail();
        String userAccount = request.getUserAccount();
        String userPassword = request.getUserPassword();
        String checkPassword = request.getCheckPassword();

        // 基本验证
        ThrowUtils.throwIf(StringUtils.isAnyBlank(email, userAccount, userPassword, checkPassword),
                ErrorCode.PARAMS_ERROR, "参数不能为空");

        // 邮箱格式验证
        ThrowUtils.throwIf(!email.matches("^[A-Za-z0-9+_.-]+@(.+)$"),
                ErrorCode.PARAMS_ERROR, "邮箱格式不正确");

        // 密码一致性验证
        ThrowUtils.throwIf(!userPassword.equals(checkPassword),
                ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");

        // 调用服务层进行注册
        long userId = userService.userRegisterByEmail(email, userAccount, userPassword);

        return ResultUtils.success(userId);
    }

    /**
     * 邮箱/用户名登录
     * 支持用户使用邮箱或用户名进行登录
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> login(@RequestBody EmailLoginRequest request, HttpServletRequest httpRequest) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        String account = request.getAccount(); // 可以是邮箱或用户名
        String password = request.getPassword();

        ThrowUtils.throwIf(StringUtils.isAnyBlank(account, password),
                ErrorCode.PARAMS_ERROR, "账号和密码不能为空");

        // 调用服务层进行登录 (移除 httpRequest 参数)
        LoginUserVO loginUserVO = userService.userLoginByEmailOrAccount(account, password, httpRequest);
        
        // 设置Sa-Token登录态
        // StpUtil.login(loginUserVO.getId());

        return ResultUtils.success(loginUserVO);
    }

    /**
     * Google OAuth登录
     * 接收前端传来的Google ID Token进行验证和登录
     */
    @PostMapping("/google/login")
    public BaseResponse<LoginUserVO> googleLogin(@RequestBody GoogleLoginRequest request, HttpServletRequest httpRequest) {
        ThrowUtils.throwIf(request == null || StringUtils.isBlank(request.getIdToken()),
                ErrorCode.PARAMS_ERROR, "Google ID Token不能为空");

        // 验证Google Token并处理登录
        User user = oAuth2LoginSuccessHandler.handleGoogleLogin(request.getIdToken());
        ThrowUtils.throwIf(user == null, ErrorCode.OPERATION_ERROR, "Google登录失败");

        // 设置Sa-Token登录态
        // StpUtil.login(user.getId());
        // 移除旧的session设置: httpRequest.getSession().setAttribute(USER_LOGIN_STATE, user);
        httpRequest.getSession().setAttribute(USER_LOGIN_STATE, user);

        // 返回登录用户信息
        LoginUserVO loginUserVO = userService.getLoginUserVO(user);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 验证邮箱
     * 用户点击邮件中的链接，携带token进行邮箱验证
     */
    @GetMapping("/verify-email")
    public BaseResponse<Boolean> verifyEmail(@RequestParam String token) {
        ThrowUtils.throwIf(StringUtils.isBlank(token), ErrorCode.PARAMS_ERROR, "验证令牌不能为空");

        boolean result = verificationTokenService.verifyEmail(token);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "邮箱验证失败，令牌可能已过期或无效");

        return ResultUtils.success(true);
    }

    /**
     * 重新发送验证邮件
     * 用户可以请求重新发送验证邮件
     */
    @PostMapping("/resend-verification")
    public BaseResponse<Boolean> resendVerification(@RequestBody ResendVerificationRequest request) {
        ThrowUtils.throwIf(request == null || StringUtils.isBlank(request.getEmail()),
                ErrorCode.PARAMS_ERROR, "邮箱不能为空");

        boolean result = userService.resendVerificationEmail(request.getEmail());
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "发送验证邮件失败");

        return ResultUtils.success(true);
    }

    /**
     * 忘记密码 - 发送重置邮件
     * 用户输入邮箱，系统发送密码重置邮件
     */
    @PostMapping("/forgot-password")
    public BaseResponse<Boolean> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        ThrowUtils.throwIf(request == null || StringUtils.isBlank(request.getEmail()),
                ErrorCode.PARAMS_ERROR, "邮箱不能为空");

        boolean result = userService.sendPasswordResetEmail(request.getEmail());
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "发送密码重置邮件失败");

        return ResultUtils.success(true);
    }

    /**
     * 重置密码
     * 用户通过邮件中的链接，携带token和新密码进行密码重置
     */
    @PostMapping("/reset-password")
    public BaseResponse<Boolean> resetPassword(@RequestBody ResetPasswordRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        String token = request.getToken();
        String newPassword = request.getNewPassword();
        String confirmPassword = request.getConfirmPassword();

        ThrowUtils.throwIf(StringUtils.isAnyBlank(token, newPassword, confirmPassword),
                ErrorCode.PARAMS_ERROR, "参数不能为空");

        ThrowUtils.throwIf(!newPassword.equals(confirmPassword),
                ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");

        ThrowUtils.throwIf(newPassword.length() < 8,
                ErrorCode.PARAMS_ERROR, "密码长度不能少于8位");

        boolean result = userService.resetPassword(token, newPassword);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "密码重置失败，令牌可能已过期或无效");

        return ResultUtils.success(true);
    }

    /**
     * 检查邮箱是否已注册
     * 前端可以在用户输入邮箱时实时检查
     */
    @GetMapping("/check-email")
    public BaseResponse<Boolean> checkEmailExists(@RequestParam String email) {
        ThrowUtils.throwIf(StringUtils.isBlank(email), ErrorCode.PARAMS_ERROR, "邮箱不能为空");

        boolean exists = userService.checkEmailExists(email);
        return ResultUtils.success(exists);
    }

    /**
     * 检查用户名是否已注册
     * 前端可以在用户输入用户名时实时检查
     */
    @GetMapping("/check-username")
    public BaseResponse<Boolean> checkUsernameExists(@RequestParam String username) {
        ThrowUtils.throwIf(StringUtils.isBlank(username), ErrorCode.PARAMS_ERROR, "用户名不能为空");

        boolean exists = userService.checkUsernameExists(username);
        return ResultUtils.success(exists);
    }
}