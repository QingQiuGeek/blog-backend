package com.serein.model.request.PassageRequest;

import java.util.Date;
import lombok.Data;

/**
 * @Author:懒大王Smile
 * @Date: 2024/11/17
 * @Time: 22:19
 * @Description:
 */

@Data
public class AdminPassageQueryPageRequest {

  //默认的页码和页大小
  private int currentPage = 1;
  private int pageSize = 10;
  private String authorName;
  private String title;
  private Long authorId;
  private Long passageId;
  private Date startTime;
  private Date endTime;

}
