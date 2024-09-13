package com.serein.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 文章表
 * @TableName passage
 */
@TableName(value ="passage")
@Data
public class Passage implements Serializable {
    /**
     * 文章id
     */
    @TableId(type = IdType.AUTO)
    private Long passageId;

    /**
     * 作者id,逻辑关联用户表
     */
    private Long authorId;

    /**
     * 作者名,逻辑关联用户表
     */
    private String authorName;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 预览图URL
     */
    private String thumbnail;

    /**
     * 内容摘要
     */
    private String summary;

    /**
     * 文章所属类别
     */
    private Integer categoryId;

    /**
     * 浏览量
     */
    private Integer viewNum;

    /**
     * 评论数量
     */
    private Integer commentNum;

    /**
     * 点赞数量
     */
    private Integer thumbNum;

    /**
     * 收藏数量
     */
    private Integer collectNum;

    /**
     * 发布时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 审核通过时间
     */
    private Date accessTime;

    /**
     * 文章状态(0草稿,1待审核,2已发布)
     */
    private Integer status;

    /**
     * 0逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}