package com.serein.model.Request;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author:懒大王Smile
 * @Date: 2024/10/22
 * @Time: 17:05
 * @Description:
 */
@Data
public class LogoutRequest implements Serializable {
    //登陆凭证
    @TableField(exist = false)
    private String token;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
