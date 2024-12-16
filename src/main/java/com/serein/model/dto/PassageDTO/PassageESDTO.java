package com.serein.model.dto.PassageDTO;

import cn.hutool.json.JSONUtil;
import com.serein.model.entity.Passage;
import com.serein.model.vo.PassageVO.PassageVO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @Author:懒大王Smile
 * @Date: 2024/10/24
 * @Time: 11:03
 * @Description: 为es搜索专门设计的DTO
 */

//index索引类似mysql的表名，和主键id不是一个东西
//索引名称必须全部为小写字母，不能包含大写字母、特殊字符或空格，否则写入es报错
/*
    * {
  "_index" : "mytest",索引
  "_type" : "_doc",
  "_id" : "tfxrv5IBmUlvKdLabTOb",主键
  "_version" : 1,
  "_seq_no" : 2,
  "_primary_term" : 1,
  "found" : true,
  "_source" : {
    "title" : "黑子2",
    "desc" : "黑子的描述2"
  }
}*/
@Document(indexName = "passage")
@Data
public class PassageESDTO implements Serializable {

//    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  //@Id能够保证数据库中的id和es的id统一，否则es会自动生成id
  @Id
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
//    private String thumbnail;

  /**
   * 内容摘要
   */
  private String summary;

  /**
   * 文章所属类别
   */
//    private Integer categoryId;

  /**
   * todo 修改了passage的标签，这里也要改
   * 标签
   */
  private Map<Long,String> pTagsMap;

  /**
   * 浏览量
   */
//    private Integer viewNum;

  /**
   * 评论数量
   */
//    private Integer commentNum;

  /**
   * 点赞数量
   */
//    private Integer thumbNum;

  /**
   * 收藏数量
   */
//    private Integer collectNum;
  /**
   * 审核通过时间
   */
//    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
//    private Date accessTime;

  /* *//**
   * 文章状态(0草稿,1待审核,2已发布)
   *//*
    private Integer status;

    */
  /**
   * 0逻辑删除
   *//*
    private Integer isDelete;*/

  private static final long serialVersionUID = 1L;


  /**
   * 对象转包装类
   *
   * @param passageVO
   * @return
   */
  public static PassageESDTO objToDto(PassageVO passageVO) {
    if (passageVO == null) {
      return null;
    }
    PassageESDTO passageESDTO = new PassageESDTO();
    BeanUtils.copyProperties(passageVO, passageESDTO);
    Map<Long, String> pTagsMap = passageVO.getPTagsMap();
    passageESDTO.setPTagsMap(pTagsMap);
    return passageESDTO;
  }

  /**
   * 包装类转对象
   *
   * @param passageESDTO
   * @return
   */
  public static Passage dtoToObj(PassageESDTO passageESDTO) {
    if (passageESDTO == null) {
      return null;
    }
    Passage passage = new Passage();
    BeanUtils.copyProperties(passageESDTO, passage);
    Map<Long, String> tagsMap = passageESDTO.getPTagsMap();
    List<Long> list = new ArrayList<>(tagsMap.keySet());
    String jsonStr = JSONUtil.toJsonStr(list);
    passage.setTagsId(jsonStr);
    return passage;
  }

}
