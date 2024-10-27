package com.serein.model.Request;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
