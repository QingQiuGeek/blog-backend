package com.serein.model.request.UserRequest;

import java.util.Date;
import lombok.Data;

/**
 * @Author:懒大王Smile
 * @Date: 2024/11/17
 * @Time: 22:19
 * @Description:
 */

@Data
public class AdminUserQueryPageRequest {

  //默认的页码和页大小
  private int currentPage = 1;
  private int pageSize = 10;

  private String mail;
  private String userName;
  private Long userId;
  private Date endTime;
  private Date startTime;
}
