package com.serein.model.dto.userDTO;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户表
 *
 * @author 懒大王Smile
 * @TableName user
 * @Date: 2024/9/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserDTO implements Serializable {


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

//  /**
//   * 预留字段，兴趣标签
//   */
  private String interestTag;

  /**
   * 预留字段，兴趣标签
   */
//  private List<String> interestTag;


  /**
   * 用户名
   */
  private String userName;

  /**
   * ip地址
   */
  private String ipAddress;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;


}