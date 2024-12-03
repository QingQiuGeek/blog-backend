package com.serein.model.request;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @Author:懒大王Smile
 * @Date: 2024/10/22
 * @Time: 12:50
 * @Description:
 */

@Data
public class GetUserByIdListRequest implements Serializable {

  List<Long> idList;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}
