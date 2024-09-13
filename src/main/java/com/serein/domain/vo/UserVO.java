package com.serein.domain.vo;

import lombok.Data;


/**
 * @Author:懒大王Smile
 * @Date: 2024/9/13
 * @Time: 13:18
 * @Description:
 */

@Data
public class UserVO  {

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 性别(0女,1男,2未知)
     */
    private Integer sex;

    /**
     * 用户简介
     */
    private String profiles;

    /**
     * 用户名,可用于登录
     */
    private String userName;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 电话,预留字段
     */
    private String phone;

    /**
     * 预留字段,用户等级
     */
    private Integer level;
}
