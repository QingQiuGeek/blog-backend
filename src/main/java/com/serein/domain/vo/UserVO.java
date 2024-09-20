package com.serein.domain.vo;

import lombok.Data;

import java.io.Serializable;


/**
 * @Author:懒大王Smile
 * @Date: 2024/9/13
 * @Time: 13:18
 * @Description:
 */

@Data
public class UserVO  implements Serializable {

    /**
     * 用户id
     */
    private Long userId;

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
     * 用户名
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

    private static final long serialVersionUID = 1L;

}
