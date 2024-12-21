package com.serein.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 文章定时发布表
 * @TableName passage_time_publish
 */
@TableName(value ="passage_time_publish")
@Data
public class PassageTimePublish implements Serializable {
    /**
     * 文章id
     */
    @TableId(type = IdType.AUTO)
    private Long passageId;

    /**
     * 发布时间
     */
    private Date publishTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}