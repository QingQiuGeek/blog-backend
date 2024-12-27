package com.serein.model.vo.passageVO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author:懒大王Smile
 * @Date: 2024/12/2
 * @Time: 17:47
 * @Description:
 */

@NoArgsConstructor
@Data
public class AdminPassageVO implements Serializable {


  /**
   * 文章id  精度丢失 后端把long类型的数据转成string类型给前端，防止精度丢失
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
   * 文章标题
   */
  private String title;

//  /**
//   * 标签列表 存到数据库的时候是json格式
//   * */
//  private List<String> pTags;


  /**
   * long是标签id，value是标签名
   */
  private Map<Long, String> pTagsMap;

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

  /**
   * 文章状态(0禁用,1待审核，2已发布，3驳回
   */
  private Integer status;

}
