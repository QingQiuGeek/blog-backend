package com.serein.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 用户-收藏表
 * @TableName user_collects
 */
@TableName(value ="user_collects")
@Data
@Builder
public class UserCollects implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 文章id
     */
    private Long passageId;

    /**
     * 收藏时间
     */
    private Date collectTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}