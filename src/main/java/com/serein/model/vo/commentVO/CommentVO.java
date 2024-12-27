package com.serein.model.vo.commentVO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;


@Data
public class CommentVO implements Serializable {

  /**
   * 评论id
   */
  private Long commentId;

  /**
   * 评论的内容
   */
  private String content;

  /**
   * 评论的用户id
   */
  private Long commentUserId;

  /**
   * 评论的文章id
   */
  @JsonSerialize(using = ToStringSerializer.class)
  private Long passageId;

  /**
   * 文章作者的id
   */
  private Long authorId;

  /**
   * 评论时间
   */
  private Date commentTime;

  /**
   * 评论的用户名
   */
  @TableField(exist = false)
  private String userName;

  /**
   * 评论的用户头像
   */
  @TableField(exist = false)
  private String avatarUrl;

  /**
   * 评论的用户ip
   */
  @TableField(exist = false)
  private String ipAddress;

  /*
   *登录用户是否可删除该评论
   */
  @TableField(exist = false)
  private Boolean canDelete = false;

  private static final long serialVersionUID = 1L;
}