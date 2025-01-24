package com.serein.model.vo.userVO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * @Author:懒大王Smile
 * @Date: 2024/10/27
 * @Time: 16:38
 * @Description: 管理员能看到的用户信息比普通用户多  role
 */

@Data
public class AdminUserVO {


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
   * 预留字段，兴趣标签
   */
  private List<String> interestTag;


  /**
   * ip地址
   */
  private String ipAddress;

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

  /**
   * 修改时间
   */
  private Date updateTime;

  /**
   * 用户状态(0禁用,1正常)
   */
  private Integer status;

}
