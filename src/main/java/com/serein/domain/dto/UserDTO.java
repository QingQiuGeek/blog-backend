package com.serein.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/19
 * @Time: 20:03
 * @Description:
 */

@Data
public class UserDTO implements Serializable {


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

    /**
     * 用户状态(0禁用,1正常)
     */
    private Integer status;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
