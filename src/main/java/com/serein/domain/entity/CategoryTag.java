package com.serein.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 类别-标签表
 * @TableName category_tag
 */
@TableName(value ="category_tag")
@Data
public class CategoryTag implements Serializable {
    /**
     * 类别id
     */
    @TableId(type = IdType.AUTO)
    private Long categoryId;

    /**
     * 标签id
     */
    private Long tagId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}