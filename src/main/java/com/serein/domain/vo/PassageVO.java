package com.serein.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/19
 * @Time: 17:47
 * @Description:
 */

@NoArgsConstructor
@Data
public class PassageVO implements Serializable {


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
     * 审核通过时间
     */
    private Date accessTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
