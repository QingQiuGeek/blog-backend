package com.serein.model.vo.TagVO;

import java.io.Serializable;
import lombok.Data;

/**
 * 标签表
 *
 * @TableName tags
 */
@Data
public class TagVO implements Serializable {

  /**
   * 标签id
   */
  private Long tagId;

  /**
   * 标签名
   */
  private String tagName;

  private static final long serialVersionUID = 1L;
}