package com.serein.model.dto.PassageDTO;

import cn.hutool.json.JSONUtil;
import com.serein.mapper.PassageTagMapper;
import com.serein.mapper.TagsMapper;
import com.serein.model.entity.Passage;
import com.serein.model.entity.PassageTag;
import com.serein.model.entity.Tags;
import com.serein.model.vo.PassageVO.PassageVO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
@Document(indexName = "passage_v2")
@Data
public class PassageESDTO implements Serializable {

  //@Id能够保证数据库中的id和es的id统一，否则es会自动生成id
  @Id
  private Long passageId;

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
   * 内容摘要
   */
  private String summary;

  private String tagStr;


  private static final long serialVersionUID = 1L;



}
