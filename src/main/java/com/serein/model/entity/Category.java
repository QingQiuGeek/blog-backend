package com.serein.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 类别表
 * @author 懒大王Smile
 * @TableName category
 */
@TableName(value ="category")
@Data
public class Category implements Serializable {
    /**
     * 类别id
     */
    @TableId(type = IdType.AUTO)
    private Long categoryId;

    /**
     * 类别名
     */
    private String categoryName;

    /**
     * 该类别下所有标签（json格式）
     */
    private String cTags;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 0逻辑删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}