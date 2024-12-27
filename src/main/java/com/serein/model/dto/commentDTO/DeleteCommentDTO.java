package com.serein.model.dto.commentDTO;

import java.io.Serializable;
import lombok.Data;


@Data
public class DeleteCommentDTO implements Serializable {


  /**
   * 评论的文章id
   */
  private String passageId;

  /**
   * 评论id
   */
  private Long commentId;

  private static final long serialVersionUID = 1L;
}