package com.serein.domain.Request;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;


@Data
public class LoginRequest implements Serializable {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 账户,可用于登录
     */
    private String userAccount;

    /**
     * 密码,可用于登录
     */
    private String password;

    /**
     * 用户名
     */
    private String userName;


    /**
     * 角色
     */
    private Integer role;

    /**
     * 邮箱
     */
    private String mail;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}