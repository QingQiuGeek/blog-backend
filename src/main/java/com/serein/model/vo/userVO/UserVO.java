package com.serein.model.vo.userVO;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;


/**
 * @Author:懒大王Smile
 * @Date: 2024/9/13
 * @Time: 13:18
 * @Description: 管理员查询用户list
 */

@Data
public class UserVO implements Serializable {

  /* 是否关注
   *
   * */
  @TableField(exist = false)
  private Boolean isFollow = false;
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
   * 粉丝数量
   */
  private Integer followerNum;

  /**
   * 用户名
   */
  private String userName;

  /**
   * 邮箱
   */
  private String mail;

  /**
   * ip地址
   */
  private String ipAddress;


  /**
   * 预留字段,用户等级
   */
  private Integer level;

  /**
   * 创建时间
   */
  private Date createTime;

  private static final long serialVersionUID = 1L;

}
