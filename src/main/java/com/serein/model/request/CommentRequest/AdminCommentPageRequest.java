package com.serein.model.request.CommentRequest;

import java.util.Date;
import lombok.Data;

/**
 * @Author:懒大王Smile
 * @Date: 2024/11/17
 * @Time: 22:19
 * @Description:
 */

@Data
public class AdminCommentPageRequest {

  //默认的页码和页大小
  private int currentPage = 1;
  private int pageSize = 10;
  private Long commentId;
  private String content;
  private Long authorId;
  private Long commentUserId;
  private Long passageId;
  private Date startTime;
  private Date endTime;


}
