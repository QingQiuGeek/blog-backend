package com.serein.model.vo.PassageVO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/19
 * @Time: 17:47
 * @Description:
 */

@NoArgsConstructor
@Data
public class PassageVO implements Serializable {


    //todo 当前用户是否点赞
    @TableField(exist = false)
    private Boolean isThumb=false;

    //todo 当前用户是否收藏
    @TableField(exist = false)
    private Boolean isCollect=false;

    /**
     * 文章id  精度丢失
     */
//    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = ToStringSerializer.class)
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
     * 头像URL
     */
    private String avatarUrl;

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

    /*
    * 标签列表 存到数据库的时候是json格式
    * */
    private List<String> pTags;

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
