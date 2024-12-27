package com.serein.model.dto.categoryDTO;

import java.io.Serializable;
import lombok.Data;

/**
 *
 */
@Data
public class CategoryDTO implements Serializable {

  private Long categoryId;

  /**
   * 类别名
   */
  private String categoryName;


  /**
   * 类别描述
   */
  private String description;

  /**
   * 创建时间
   */
  private Long createTime;

  /**
   * 修改时间
   */
  private Long updateTime;


  private static final long serialVersionUID = 1L;
}