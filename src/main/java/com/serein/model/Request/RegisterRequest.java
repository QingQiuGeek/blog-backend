package com.serein.model.Request;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;


/**
 * @author 懒大王Smile
 */
@Data
public class RegisterRequest implements Serializable {





    /**
     * 密码,可用于登录
     */
    private String password;

    /**
     * 重复输入密码
     */
    private String rePassword;


    /**
     * 用户名
     */
    private String userName;


    /**
     * 邮箱
     */
    private String mail;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}