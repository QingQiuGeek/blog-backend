package com.serein.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户关注表
 * @TableName userfollow
 */
@TableName(value ="userfollow")
@Data
public class Userfollow implements Serializable {
    /**
     * 关注id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关注的用户id
     */
    private Long userId;

    /**
     * 被关注的用户id
     */
    private Long toUserId;

    /**
     * 关注时间
     */
    private Date followTime;

    /**
     * 0逻辑删除取消关注
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}