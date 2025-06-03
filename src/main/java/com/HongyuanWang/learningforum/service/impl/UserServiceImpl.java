package com.HongyuanWang.learningforum.service.impl;

import static com.HongyuanWang.learningforum.constant.UserConstant.USER_LOGIN_STATE;

import cn.hutool.core.collection.CollUtil;
import com.HongyuanWang.learningforum.service.EmailService;
import com.HongyuanWang.learningforum.service.VerificationTokenService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.HongyuanWang.learningforum.common.ErrorCode;
import com.HongyuanWang.learningforum.constant.CommonConstant;
import com.HongyuanWang.learningforum.exception.BusinessException;
import com.HongyuanWang.learningforum.mapper.UserMapper;
import com.HongyuanWang.learningforum.model.dto.user.UserQueryRequest;
import com.HongyuanWang.learningforum.model.entity.User;
import com.HongyuanWang.learningforum.model.enums.UserRoleEnum;
import com.HongyuanWang.learningforum.model.vo.LoginUserVO;
import com.HongyuanWang.learningforum.model.vo.UserVO;
import com.HongyuanWang.learningforum.service.UserService;
import com.HongyuanWang.learningforum.utils.SqlUtils;
import com.HongyuanWang.learningforum.constant.RedisConstant;
import com.HongyuanWang.learningforum.service.SubscriptionService;
import com.HongyuanWang.learningforum.model.entity.Subscription;
import com.HongyuanWang.learningforum.model.enums.SubscriptionPlan;
import com.HongyuanWang.learningforum.model.enums.SubscriptionStatus;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBitSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

/**
 * 用户服务实现
 *
 * @author <a href="https://github.com/HongyuanWang">Hongyuan Wang</a>
 * @from <a href="https://HongyuanWang.icu">学习刷题论坛</a>
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    public RedissonClient redissonClient;

    @Resource
    private EmailService emailService;

    @Resource
    private VerificationTokenService verificationTokenService;

    @Resource
    @Lazy
    private SubscriptionService subscriptionService;
    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "HongyuanWang";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // Revert to HttpSession
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    @Override
    public LoginUserVO userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request) {
        String unionId = wxOAuth2UserInfo.getUnionId();
        String mpOpenId = wxOAuth2UserInfo.getOpenid();
        // 单机锁
        synchronized (unionId.intern()) {
            // 查询用户是否已存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("unionId", unionId);
            User user = this.getOne(queryWrapper);
            // 被封号，禁止登录
            if (user != null && UserRoleEnum.BAN.getValue().equals(user.getUserRole())) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "该用户已被封，禁止登录");
            }
            // 用户不存在则创建
            if (user == null) {
                user = new User();
                user.setUnionId(unionId);
                user.setMpOpenId(mpOpenId);
                user.setUserAvatar(wxOAuth2UserInfo.getHeadImgUrl());
                user.setUserName(wxOAuth2UserInfo.getNickname());
                boolean result = this.save(user);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败");
                }
            }
            // Revert to HttpSession
            request.getSession().setAttribute(USER_LOGIN_STATE, user);
            return getLoginUserVO(user);
        }
    }

    /**
     * 获取当前登录用户
     *
     * @return User
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // Re-fetch from DB to ensure data is up-to-date (consistent with original logic)
        currentUser = this.getById(currentUser.getId());
        if (currentUser == null) {
             // Session might hold an old ID for a user that has been deleted
            request.getSession().removeAttribute(USER_LOGIN_STATE); 
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户不存在或状态已失效");
        }
        return currentUser;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @return User or null
     */
    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        return this.getById(currentUser.getId()); // Consistent with original logic
    }

    /**
     * 是否为管理员
     *
     * @return boolean
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }


    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @return boolean
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            // Consider if throwing an error is desired, or just returning false/true.
            // For consistency with how it might have behaved, let's assume it can be called even if not logged in.
            // Original UserController checked for request == null, not if session attribute existed.
        }
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        // 获取并设置当前订阅计划
        try {
            Subscription activeSubscription = subscriptionService.getActiveSubscriptionByUserId(user.getId());
            if (activeSubscription != null && 
                (SubscriptionStatus.ACTIVE.getValue().equals(activeSubscription.getStatus()) || 
                 SubscriptionStatus.TRIALING.getValue().equals(activeSubscription.getStatus()))) {
                SubscriptionPlan plan = SubscriptionPlan.getEnumByValue(activeSubscription.getPlan());
                if (plan != null) {
                    userVO.setCurrentSubscriptionPlan(plan.getDescription()); // 或者 plan.getValue()，取决于您想显示什么
                } else {
                     userVO.setCurrentSubscriptionPlan("未知计划"); // 或者留空
                }
            } else {
                userVO.setCurrentSubscriptionPlan("未订阅"); // 或者留空
            }
        } catch (Exception e) {
            log.error("获取用户 {} 的订阅计划失败: {}", user.getId(), e.getMessage());
            userVO.setCurrentSubscriptionPlan("获取失败"); // 发生错误时的回退值
        }
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpenId", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取用户签到记录id
     *
     * @param userId 用户 id
     * @return 当前用户是否已经签到成功
     */
    @Override
    public boolean addUserSignIn(long userId) {
        LocalDate date = LocalDate.now();
        String key = RedisConstant.getUserSignInRedisKey(date.getYear(),userId);
        // get redis's bitmap
        RBitSet signInBitSet = redissonClient.getBitSet(key);
        int offset = date.getDayOfYear();

        if (!signInBitSet.get(offset)) {
            signInBitSet.set(offset,true);
        }

        return true;
    }

    //获取用户某个年份的签到记录
    @Override
    public List<Integer> getUserSignInRecord(long userId, Integer year){
        if (year == null) {
            LocalDate localDate = LocalDate.now();
            year = localDate.getYear();
        }
        String key = RedisConstant.getUserSignInRedisKey(year, userId);
        RBitSet signInBitSet = redissonClient.getBitSet(key);

        //优先把bitset加载到java内存中,避免后续循环多次访问redis
        BitSet bitSet = signInBitSet.asBitSet();
        //统计签到的日期
        List<Integer> dayList = new ArrayList<>();

        //从索引0开始查找下一个被设置为1的位
        int index = bitSet.nextSetBit(0);
        while(index >= 0){
            dayList.add(index);
            //继续查找下一个被设置为1的位
            index = bitSet.nextSetBit(index+1);
        }
        return dayList;
    }

    @Override
    public User getUserByStripeCustomerId(String stripeCustomerId) {
        if (StringUtils.isBlank(stripeCustomerId)) {
            return null;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("stripeCustomerId", stripeCustomerId);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional
    public long userRegisterByEmail(String email, String userAccount, String userPassword) {
        // 1. 验证参数
        if (StringUtils.isAnyBlank(email, userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        // 2. 验证邮箱格式
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式不正确");
        }

        // 3. 验证账号长度
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }

        // 4. 验证密码长度
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }

        synchronized (userAccount.intern()) {
            // 5. 检查账号是否已存在
            QueryWrapper<User> accountWrapper = new QueryWrapper<>();
            accountWrapper.eq("userAccount", userAccount);
            if (this.count(accountWrapper) > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已被注册");
            }

            // 6. 检查邮箱是否已存在
            QueryWrapper<User> emailWrapper = new QueryWrapper<>();
            emailWrapper.eq("email", email);
            if (this.count(emailWrapper) > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱已被注册");
            }

            // 7. 加密密码
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

            // 8. 创建用户（未激活状态）
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setEmail(email);
            user.setUserName(userAccount); // 默认昵称为账号
            user.setActivated(false); // 邮箱未验证
            user.setAuthType("password"); // 认证类型

            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
            }

            // 9. 创建验证令牌并发送邮件
            String token = verificationTokenService.createEmailVerificationToken(user.getId());
            emailService.sendVerificationEmail(email, token, userAccount);

            log.info("用户注册成功: userId={}, userAccount={}, email={}", user.getId(), userAccount, email);
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLoginByEmailOrAccount(String account, String password, HttpServletRequest request) {
        // 1. 参数验证
        if (StringUtils.isAnyBlank(account, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        if (password.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }

        // 2. 加密密码
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        // 3. 查询用户（支持邮箱或用户名）
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (account.contains("@")) {
            // 邮箱登录
            queryWrapper.eq("email", account);
        } else {
            // 用户名登录
            queryWrapper.eq("userAccount", account);
        }
        queryWrapper.eq("userPassword", encryptPassword);

        User user = this.baseMapper.selectOne(queryWrapper);

        // 4. 用户不存在或密码错误
        if (user == null) {
            log.info("用户登录失败: account={}", account);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

        // 5. 检查是否被封号
        if (UserRoleEnum.BAN.getValue().equals(user.getUserRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "该账号已被封禁");
        }

        // 6. 检查邮箱是否已验证（仅对密码认证类型的用户）
        if ("password".equals(user.getAuthType()) && (user.getActivated() == null || !user.getActivated())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "请先验证邮箱后再登录");
        }

        // Revert to HttpSession
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        log.info("用户登录成功: userId={}, account={}", user.getId(), account);
        return this.getLoginUserVO(user);
    }

    @Override
    @Transactional
    public User findOrCreateGoogleUser(String googleId, String email, boolean emailVerified,
                                       String name, String pictureUrl) {
        // 1. 先通过Google ID查找用户
        QueryWrapper<User> googleWrapper = new QueryWrapper<>();
        googleWrapper.eq("google_id", googleId);
        User user = this.baseMapper.selectOne(googleWrapper);

        if (user != null) {
            // 用户已存在，更新信息
            boolean needUpdate = false;

            // 更新头像
            if (StringUtils.isNotBlank(pictureUrl) && !pictureUrl.equals(user.getUserAvatar())) {
                user.setUserAvatar(pictureUrl);
                needUpdate = true;
            }

            // 更新邮箱验证状态
            if (emailVerified && !user.getActivated()) {
                user.setActivated(true);
                user.setEmailVerifiedAt(new Date());
                needUpdate = true;
            }

            if (needUpdate) {
                this.updateById(user);
            }

            return user;
        }

        // 2. 用户不存在，检查邮箱是否已被其他方式注册
        QueryWrapper<User> emailWrapper = new QueryWrapper<>();
        emailWrapper.eq("email", email);
        user = this.baseMapper.selectOne(emailWrapper);

        if (user != null) {
            // 邮箱已存在，关联Google账号
            user.setGoogleId(googleId);
            if (StringUtils.isNotBlank(pictureUrl)) {
                user.setUserAvatar(pictureUrl);
            }
            if (emailVerified) {
                user.setActivated(true);
                user.setEmailVerifiedAt(new Date());
            }
            this.updateById(user);

            log.info("已有邮箱用户关联Google账号: userId={}, email={}", user.getId(), email);
            return user;
        }

        // 3. 创建新用户
        user = new User();
        user.setGoogleId(googleId);
        user.setEmail(email);
        user.setUserName(name != null ? name : "Google用户");
        user.setUserAvatar(pictureUrl);
        user.setActivated(emailVerified);
        user.setAuthType("google");

        // 生成唯一的用户账号
        String userAccount = generateUniqueUserAccount(email);
        user.setUserAccount(userAccount);

        // Google用户不需要密码，但为了数据库完整性，设置一个随机密码
        String randomPassword = UUID.randomUUID().toString();
        user.setUserPassword(DigestUtils.md5DigestAsHex((SALT + randomPassword).getBytes()));

        if (emailVerified) {
            user.setEmailVerifiedAt(new Date());
        }

        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建用户失败");
        }

        // 发送欢迎邮件
        if (emailVerified) {
            emailService.sendWelcomeEmail(email, user.getUserName());
        }

        log.info("Google用户注册成功: userId={}, email={}, googleId={}", user.getId(), email, googleId);
        return user;
    }

    @Override
    public boolean resendVerificationEmail(String email) {
        // 1. 查找用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        User user = this.baseMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "该邮箱未注册");
        }

        // 2. 检查是否已验证
        if (user.getActivated()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该邮箱已验证");
        }

        // 3. 检查是否为Google用户
        if ("google".equals(user.getAuthType())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Google账号无需邮箱验证");
        }

        // 4. 创建新的验证令牌并发送邮件
        String token = verificationTokenService.createEmailVerificationToken(user.getId());
        emailService.sendVerificationEmail(email, token, user.getUserName());

        log.info("重新发送验证邮件: userId={}, email={}", user.getId(), email);
        return true;
    }

    @Override
    public boolean sendPasswordResetEmail(String email) {
        // 1. 查找用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        User user = this.baseMapper.selectOne(queryWrapper);

        if (user == null) {
            // 出于安全考虑，即使邮箱不存在也返回成功
            log.warn("尝试重置不存在的邮箱密码: email={}", email);
            return true;
        }

        // 2. 检查是否为Google用户
        if ("google".equals(user.getAuthType())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Google账号无法重置密码");
        }

        // 3. 创建密码重置令牌并发送邮件
        String token = verificationTokenService.createPasswordResetToken(user.getId());
        emailService.sendPasswordResetEmail(email, token, user.getUserName());

        log.info("发送密码重置邮件: userId={}, email={}", user.getId(), email);
        return true;
    }

    @Override
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        // 1. 验证令牌并获取用户ID
        Long userId = verificationTokenService.validatePasswordResetToken(token);
        if (userId == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "令牌无效或已过期");
        }

        // 2. 获取用户
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 3. 更新密码
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + newPassword).getBytes());
        user.setUserPassword(encryptPassword);

        boolean updateResult = this.updateById(user);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "密码重置失败");
        }

        // 4. 标记令牌为已使用
        verificationTokenService.markTokenAsUsed(token);

        log.info("密码重置成功: userId={}", userId);
        return true;
    }

    @Override
    public boolean checkEmailExists(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return this.count(queryWrapper) > 0;
    }

    @Override
    public boolean checkUsernameExists(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", username);
        return this.count(queryWrapper) > 0;
    }

    /**
     * 根据邮箱生成唯一的用户账号
     */
    private String generateUniqueUserAccount(String email) {
        // 使用邮箱前缀作为基础
        String baseAccount = email.substring(0, email.indexOf("@"));
        baseAccount = baseAccount.replaceAll("[^a-zA-Z0-9]", ""); // 移除特殊字符

        // 确保长度至少为4
        if (baseAccount.length() < 4) {
            baseAccount = "user" + baseAccount;
        }

        // 检查是否已存在，如果存在则添加数字后缀
        String userAccount = baseAccount;
        int suffix = 1;

        while (checkUsernameExists(userAccount)) {
            userAccount = baseAccount + suffix;
            suffix++;
        }

        return userAccount;
    }
}
