package com.serein.model.request.CommentRequest;

import lombok.Data;

/**
 * @Author:懒大王Smile
 * @Date: 2024/11/17
 * @Time: 22:19
 * @Description:
 */

@Data
public class CursorCommentRequest {

  private Long authorId;
  private Long passageId;
  private Long lastCommentId;
  private Integer pageSize = 10;


}
