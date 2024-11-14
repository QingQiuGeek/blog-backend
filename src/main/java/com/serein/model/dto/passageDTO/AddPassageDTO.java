package com.serein.model.dto.passageDTO;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: 懒大王Smile
 * @Date: 2024/9/19
 * @Time: 17:47
 * @Description:
 */

@NoArgsConstructor
@Data
public class AddPassageDTO extends PassageDTO implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
