package com.serein.model.dto.passageDTO;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.serein.model.entity.Passage;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
     * 标签
     */
    private List<String> pTags;

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

    *//**
     * 0逻辑删除
     *//*
    private Integer isDelete;*/

    private static final long serialVersionUID = 1L;

    /**
     * 对象转包装类
     *
     * @param passage
     * @return
     */
    public static PassageESDTO objToDto(Passage passage) {
        if (passage == null) {
            return null;
        }
        PassageESDTO passageESDTO = new PassageESDTO();
        BeanUtils.copyProperties(passage, passageESDTO);
        String tagsStr = passage.getPTags();
        if (StringUtils.isNotBlank(tagsStr)) {
            passageESDTO.setPTags(JSONUtil.toList(tagsStr, String.class));
        }
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
        Passage post = new Passage();
        BeanUtils.copyProperties(passageESDTO, post);
        List<String> tagList = passageESDTO.getPTags();
        if (CollUtil.isNotEmpty(tagList)) {
            post.setPTags(JSONUtil.toJsonStr(tagList));
        }
        return post;
    }

}
