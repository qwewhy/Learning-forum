package com.HongyuanWang.learningforum.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.HongyuanWang.learningforum.model.dto.user.UserQueryRequest;
import com.HongyuanWang.learningforum.model.entity.User;
import com.HongyuanWang.learningforum.model.vo.LoginUserVO;
import com.HongyuanWang.learningforum.model.vo.UserVO;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

/**
 * 用户服务
 *
 * @author <a href="https://github.com/HongyuanWang">Hongyuan Wang</a>
 * @from <a href="https://HongyuanWang.icu">学习刷题论坛</a>
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户登录（微信开放平台）
     *
     * @param wxOAuth2UserInfo 从微信获取的用户信息
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @return User
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @return User or null
     */
    User getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @return boolean
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     * 用户注销
     *
     * @return boolean
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 获取用户签到记录id
     *
     * @param userId 用户 id
     * @return 当前用户是否已经签到成功
     */
    boolean addUserSignIn(long userId);

    //获取用户某个年份的签到记录
    List<Integer> getUserSignInRecord(long userId, Integer year);

    /**
     * 根据 Stripe Customer ID 获取用户
     *
     * @param stripeCustomerId Stripe Customer ID
     * @return 用户实体，如果找不到则返回 null
     */
    User getUserByStripeCustomerId(String stripeCustomerId);

    /**
     * 邮箱注册
     */
    long userRegisterByEmail(String email, String userAccount, String userPassword);

    /**
     * 邮箱或用户名登录
     */
    LoginUserVO userLoginByEmailOrAccount(String account, String password, HttpServletRequest request);

    /**
     * 查找或创建Google用户
     */
    User findOrCreateGoogleUser(String googleId, String email, boolean emailVerified,
                                String name, String pictureUrl);

    /**
     * 重新发送验证邮件
     */
    boolean resendVerificationEmail(String email);

    /**
     * 发送密码重置邮件
     */
    boolean sendPasswordResetEmail(String email);

    /**
     * 重置密码
     */
    boolean resetPassword(String token, String newPassword);

    /**
     * 检查邮箱是否存在
     */
    boolean checkEmailExists(String email);

    /**
     * 检查用户名是否存在
     */
    boolean checkUsernameExists(String username);
}
