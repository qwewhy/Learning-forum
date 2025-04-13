package com.HongyuanWang.learningforum.constant;

public interface RedisConstant {

    String USER_SIGN_IN_KEY_PREFIX = "user:signins";

    // 获取region 用户签到的redis key
    // @param year 年份
    // @param userId 用户id
    // @return redis key
    static String getUserSignInRedisKey(int year, long userId) {
        return String.format("%s:%s:%s", USER_SIGN_IN_KEY_PREFIX, year, userId);
    }
}
