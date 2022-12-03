package com.anyi.reggie.common;

import com.anyi.reggie.entity.Employee;

/**
 * 基于ThreadLocal的封装类，用于保存和获取当前登录用户的id
 */
public class UserContext {
    // 全局使用
    public static ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    public static Long getUserId() {

        return USER_ID.get();
    }

    public static void setUserId(Long userId) {

        USER_ID.set(userId);
    }
}
