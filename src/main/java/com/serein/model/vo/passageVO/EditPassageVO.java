package com.serein.model.vo.passageVO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/19
 * @Time: 17:47
 * @Description:
 */

@NoArgsConstructor
@Data
public class EditPassageVO implements Serializable {

  /**
   * 文章id  精度丢失
   */
//    @JsonFormat(shape = JsonFormat.Shape.STRING)
  @JsonSerialize(using = ToStringSerializer.class)
  private Long passageId;

  /**
   * 文章标题
   */
  private String title;

  /**
   * 预览图URL
   */
  private String thumbnail;

  /**
   * 内容摘要
   */
  private String summary;

  /**
   * 标签id
   */
  private List<Long> pTags;

  private String content;

  /**
   * 状态
   */
  private int status;

  private static final long serialVersionUID = 1L;
}
