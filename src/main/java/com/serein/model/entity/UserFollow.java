package com.serein.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Builder;
import lombok.Data;

/**
 * 用户关注表
 *
 * @TableName user_follow
 */
@TableName(value = "user_follow")
@Data
@Builder
public class UserFollow implements Serializable {

  /**
   * 主键id
   */
  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * 关注的用户id
   */
  private Long userId;

  /**
   * 被关注的用户id
   */
  private Long toUserId;

  /**
   * 关注时间
   */
  private Date followTime;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}