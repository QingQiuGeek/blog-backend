package com.serein.model.vo.PassageVO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/19
 * @Time: 17:47
 * @Description:
 */

@NoArgsConstructor
@Data
public class PassageContentVO implements Serializable {

    /**
     * 文章id  精度丢失
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private String passageId;

    /**
     * 作者id,逻辑关联用户表
     */
    private Long authorId;

    /**
     * 文章内容
     */
    private String content;

    @Override
    public String toString() {
        return "PassageContentVO{" +
                "passageId='" + passageId + '\'' +
                ", authorId=" + authorId +
                ", content='" + content + '\'' +
                '}';
    }

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
