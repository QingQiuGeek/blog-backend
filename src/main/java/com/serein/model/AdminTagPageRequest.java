package com.serein.model;

import java.util.Date;
import lombok.Data;

/**
 * @Author:懒大王Smile
 * @Date: 2024/11/17
 * @Time: 22:19
 * @Description:
 */

@Data
public class AdminTagPageRequest {

  //默认的页码和页大小
  private int currentPage = 1;
  private int pageSize = 10;
  private String tagName;
  private Long tagId;
  private Long categoryId;
  private Date startTime;
  private Date endTime;

}
