package com.serein.model.dto.userDTO;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/19
 * @Time: 20:03
 * @Description:
 */

@Data
public class AddUserDTO implements Serializable {


  /**
   * 邮箱
   */
  private String mail;


  @TableField(exist = false)
  private static final long serialVersionUID = 1L;

}
