package com.serein.model.request.UserRequest;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;


/**
 * @author 懒大王Smile
 */
@Data
public class RegisterCodeRequest implements Serializable {

  /**
   * 邮箱
   */
  private String mail;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}