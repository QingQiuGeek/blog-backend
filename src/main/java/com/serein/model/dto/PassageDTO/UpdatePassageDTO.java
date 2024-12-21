package com.serein.model.dto.PassageDTO;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
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
public class UpdatePassageDTO extends PassageDTO implements Serializable {


  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}
