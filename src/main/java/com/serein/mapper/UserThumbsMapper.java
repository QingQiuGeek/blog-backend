package com.serein.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serein.model.entity.UserThumbs;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author 懒大王Smile
 * @description 针对表【user_thumbs(用户-点赞表)】的数据库操作Mapper
 * @createDate 2024-10-25 23:11:12
 * @Entity com.serein.model.entity.UserThumbs
 */
@Mapper
public interface UserThumbsMapper extends BaseMapper<UserThumbs> {

  @Delete("delete from blog.user_thumbs where passageId=#{passageId}")
  void deleteByPassageId(Long passageId);

  @Select("select count(*) from blog.user_thumbs where passageId=#{passageId}")
  int count(Long passageId);
}




