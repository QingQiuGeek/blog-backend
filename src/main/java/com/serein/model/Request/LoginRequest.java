package com.serein.model.Request;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;


/**
 * @author 懒大王Smile
 */
@Data
public class LoginRequest implements Serializable {


    //登陆凭证
    @TableField(exist = false)
    private String token;

    /**
     * 密码,可用于登录
     */
    private String password;


    /**
     * 邮箱
     */
    private String mail;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}