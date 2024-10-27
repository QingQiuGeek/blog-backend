package com.serein.model.Request;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author:懒大王Smile
 * @Date: 2024/10/22
 * @Time: 12:36
 * @Description:
 */

@Data
public class SearchRequest implements Serializable {

    String searchText;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
