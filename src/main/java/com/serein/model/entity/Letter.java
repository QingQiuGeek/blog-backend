package com.serein.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 私信表
 * @TableName letter
 */
@TableName(value ="letter")
@Data
public class Letter implements Serializable {
    /**
     * 私信id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 私信的用户id
     */
    private Long userId;

    /**
     * 被私信的用户id
     */
    private Long toUserId;

    /**
     * 私信时间
     */
    private Date letterTime;

    /**
     * 私信内容
     */
    private String content;

    /**
     * 0逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}