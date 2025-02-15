package com.serein.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 文章表
 *
 * @author 懒大王Smile
 * @TableName passage
 */
@TableName(value = "passage")
@Data
public class Passage implements Serializable {

  /**
   * 文章id
   */
  @TableId(type = IdType.ASSIGN_ID)
  private Long passageId;

  /**
   * 作者id,逻辑关联用户表
   */
  private Long authorId;


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
   * 浏览量
   */
  private Integer viewNum;

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
   * 文章状态(0草稿,1待审核,2已发布,3驳回)
   */
  private Integer status;

  /**
   * 是否私密
   */
  private Integer isPrivate;

  /**
   * 0逻辑删除
   */
  @TableLogic
  private Integer isDelete;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}