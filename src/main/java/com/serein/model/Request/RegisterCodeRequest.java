package com.serein.model.Request;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;


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