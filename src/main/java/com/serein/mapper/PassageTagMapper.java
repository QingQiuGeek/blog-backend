package com.serein.mapper;

import com.serein.model.entity.PassageTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* @author 懒大王Smile
* @description 针对表【passage_tag(文章标签表)】的数据库操作Mapper
* @createDate 2024-12-19 11:34:11
* @Entity com.serein.model.entity.PassageTag
*/
@Mapper
public interface PassageTagMapper extends BaseMapper<PassageTag> {

  @Delete("delete from blog.passage_tag where passageId=#{passageId}")
  boolean deleteByPassageId(Long passageId);

  boolean insertPassageTags(@Param("tagIds") List<Long> tagIds, @Param("newPassageId") Long newPassageId);

  @Select("select tagId from blog.passage_tag where passageId=#{passageId}")
  List<PassageTag> selectTagIdByPassageId(Long passageId);


  @Delete("delete from blog.passage_tag where passageId=#{passageId}")
  void deleteTagByPassageId(Long passageId);
}




