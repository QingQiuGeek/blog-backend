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
public class ParentPassageDTO implements Serializable {


  private String passageId;

  //定时发布的时间
  private Long publishTime;

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
   * 状态
   */
  private int status;

  /**
   * 文章标签id
   */
  private List<Long> tagIdList;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}
