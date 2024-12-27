package com.serein.model.vo.categoryVO;

import java.io.Serializable;
import lombok.Data;

/**
 * 类别表
 *
 * @author 懒大王Smile
 * @TableName category
 */
@Data
public class CategoryVO implements Serializable {

  /**
   * 类别id
   */
  private Long categoryId;

  /**
   * 类别名
   */
  private String categoryName;


  /**
   * 类别描述
   */
  private String description;


  private static final long serialVersionUID = 1L;
}