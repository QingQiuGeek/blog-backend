package com.serein.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户-收藏表
 * @TableName user_collects
 */
@TableName(value ="user_collects")
@Data
public class UserCollects implements Serializable {
    /**
     * 用户id
     */
    @TableId(type = IdType.AUTO)
    private Long userId;

    /**
     * 文章id
     */
    private Long passageId;

    /**
     * 0取消收藏
     */
    @TableLogic
    private Integer collectStatus;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}