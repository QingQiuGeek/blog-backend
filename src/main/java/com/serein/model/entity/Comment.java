package com.serein.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 评论表
 * @TableName comment
 */
@TableName(value ="comment")
@Data
public class Comment implements Serializable {
    /**
     * 评论id
     */
    @TableId(type = IdType.AUTO)
    private Long commentId;

    /**
     * 评论的内容
     */
    private String content;

    /**
     * 评论的用户id
     */
    private Long commentUserId;

    /**
     * 评论的文章id 
     */
    private Long passageId;

    /**
     * 评论时间
     */
    private Date commentTime;

    /**
     * 回复目标评论id
     */
    private Long toCommentId;

    /**
     * 回复目标用户id
     */
    private Long toCommentUserId;


    /**
     * 0逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}