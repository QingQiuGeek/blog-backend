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
 * 用户-点赞表
 *
 * @TableName user_thumbs
 */
@TableName(value = "user_thumbs")
@Data
@Builder
public class UserThumbs implements Serializable {

  /**
   * 主键id
   */
  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * 用户id
   */
  private Long userId;

  /**
   * 文章id
   */
  private Long passageId;

  /**
   * 点赞时间
   */
  private Date thumbTime;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}