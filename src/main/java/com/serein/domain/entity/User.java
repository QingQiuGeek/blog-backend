package com.serein.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
@Builder
public class User implements Serializable {
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
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
     * 用户名,可用于登录
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 电话,预留字段
     */
    private String phone;

    /**
     * 角色(0普通用户,1管理员)
     */
    private Integer role;

    /**
     * 预留字段
     */
    private String accessKey;

    /**
     * 预留字段
     */
    private String secretKey;

    /**
     * 预留字段,用户等级
     */
    private Integer level;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 用户状态(0禁用,1正常)
     */
    private Integer status;

    /**
     * 0逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}