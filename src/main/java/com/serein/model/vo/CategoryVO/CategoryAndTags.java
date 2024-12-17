package com.serein.model.vo.CategoryVO;

import com.serein.model.vo.TagVO.TagVO;
import java.util.List;
import lombok.Data;

/**
 * @Author:懒大王Smile
 * @Date: 2024/12/17
 * @Time: 20:39
 * @Description:
 */

@Data
public class CategoryAndTags {

  /**
   * 类别id
   */
  private Long categoryId;
  /**
   * 类别名
   */
  private String categoryName;

  /**
   * 该category的所有tags
   */
  private List<TagVO> tagVOList;

}
