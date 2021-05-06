package com.lwh147.rtms.data.model;

/**
 * 登陆用户信息封装类
 */
public class LoggedInUser {
    /**
     * 用户id
     **/
    private final Long userId;
    /**
     * 用户名字
     **/
    private final String displayName;
    /**
     * 用户电话
     **/
    private final String phone;

    public LoggedInUser(String userId, String displayName, String phone) {
        this.userId = Long.getLong(userId);
        this.displayName = displayName;
        this.phone = phone;
    }

    public Long getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPhone() {
        return phone;
    }
}