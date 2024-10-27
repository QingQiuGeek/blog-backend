package com.serein.model.vo.UserVO;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @Author:懒大王Smile
 * @Date: 2024/10/21
 * @Time: 18:40
 * @Description:
 */

@Data
public class LoginUserVO {


    //登陆凭证
     @TableField(exist = false)
     private String token;

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
     * 预留字段，兴趣标签
     */
    private List<String> interestTag;

    /**
     * 用户名
     */
    private String userName;


    /**
     * 邮箱
     */
    private String mail;

    /**
     * 角色(user普通用户,admin管理员)
     */
    private String role;

    /**
     * 创建时间
     */
    private Date createTime;

}
