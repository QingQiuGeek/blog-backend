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
public class UpdatePassageDTO extends PassageDTO implements Serializable{

    /**
     * 文章id
     * 更新或添加文章时，需要把该属性复制给passage实体类，这样才能add后返回passageId、根据id更新文章
     */
    private String passageId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
