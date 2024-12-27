package com.serein.model.dto.tagDTO;

import java.io.Serializable;
import lombok.Data;


@Data
public class TagDTO implements Serializable {

  /**
   * 标签id
   */
  private Long tagId;

  /**
   * 标签名
   */
  private String tagName;

  /**
   * 所属类别id
   */
  private Long categoryId;

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