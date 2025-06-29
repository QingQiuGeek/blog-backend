package com.serein.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serein.model.entity.Tags;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author 懒大王Smile
 * @description 针对表【tags(标签表)】的数据库操作Mapper
 * @createDate 2024-12-03 18:38:58
 * @Entity generator.domain.Tags
 */
@Mapper
public interface TagsMapper extends BaseMapper<Tags> {


  List<Tags> getRandomTags();

  @Delete("delete from blog.tags where categoryId=#{categoryId}")
  int deleteByCategoryId(Long categoryId);

  void insertTag(Tags tags);

  boolean updateTag(Tags tag);

  @Select("select categoryId,tagName,tagId from blog.tags ")
  List<Tags> getAllTags();
}




