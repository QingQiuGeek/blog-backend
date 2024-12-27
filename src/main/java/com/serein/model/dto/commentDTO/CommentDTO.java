package com.serein.model.dto.commentDTO;

import java.io.Serializable;
import lombok.Data;


@Data
public class CommentDTO implements Serializable {


  /**
   * 评论的内容
   */
  private String content;

  /**
   * 评论的文章id
   */
  private String passageId;

  /**
   * 文章作者的id
   */
  private Long authorId;

  /**
   * 评论时间
   */
  private Long commentTime;


  private static final long serialVersionUID = 1L;
}