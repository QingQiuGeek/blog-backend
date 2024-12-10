package com.serein.model.vo.PassageVO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import lombok.Data;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/19
 * @Time: 17:47
 * @Description:
 */

@Data
//@Builder
public class PassageTitleVO implements Serializable {

  /**
   * 文章id
   */
  @JsonSerialize(using = ToStringSerializer.class)
  private Long passageId;

  /**
   * 作者id,逻辑关联用户表
   */
  private Long authorId;

  /**
   * 文章标题
   */
  private String title;


  private static final long serialVersionUID = 1L;
}
