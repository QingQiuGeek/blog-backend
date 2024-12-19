package com.serein.model.request;

import java.util.List;
import lombok.Data;

/**
 * @Author:懒大王Smile
 * @Date: 2024/10/24
 * @Time: 21:09
 * @Description:
 */
@Data
public class SearchPassageRequest {

  private String searchText;

  private String searchType;

  private Long id;

  private Integer currentPage=1;

  private Integer pageSize=5;

}
