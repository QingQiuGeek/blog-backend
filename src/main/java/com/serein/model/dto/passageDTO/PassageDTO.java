package com.serein.model.dto.passageDTO;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: 懒大王Smile
 * @Date: 2024/9/19
 * @Time: 17:47
 * @Description:
 */

@NoArgsConstructor
@Data
public class PassageDTO implements Serializable {

  /**
   * 文章标题
   */
  private String title;

  /**
   * 文章内容
   */
  private String content;

  /**
   * 预览图URL
   */
  private String thumbnail;

  /**
   * 内容摘要
   */
  private String summary;

  /**
   * 文章所属类别
   */
  private Integer categoryId;

  /**
   * 文章标签
   */
  private List<String> pTags;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}
