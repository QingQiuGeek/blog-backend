package com.serein.domain.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;


@Data
public class LoginUserDTO implements Serializable {


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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}