package com.serein.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serein.model.entity.Passage;
import com.serein.model.vo.passageVO.PassageContentVO;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author 懒大王Smile
 * @description 针对表【passage(文章表)】的数据库操作Mapper
 * @createDate 2024-09-12 22:19:13
 * @Entity com.serein.domain.entity.Passage
 */
@Mapper
public interface PassageMapper extends BaseMapper<Passage> {

  List<Passage> asyncPassageToEs(Date minutesAgoDate);

  //根据uid查询该用户收藏的文章数量
  @Select("select count(*) from blog.user_collects where userId=#{uid}")
  Integer getCollectNumById(Long uid);

  @Select("select count(*) from blog.passage where authorId=#{uid}")
  Integer getPassageNumById(Long uid);

  void updateViewNum(Long passageId);

  @Select("select content from blog.passage where passageId=#{pid} and authorId=#{uid} and isPrivate=1")
  PassageContentVO getPassageContentByPid(@Param("uid") Long uid, @Param("pid") Long pid);


  @Select("select passageId,authorId,viewNum,accessTime,title,summary from blog.passage where passageId=#{passageId} and isPrivate=1")
  Passage getPassageInfo(Long passageId);

  @Select("select count(*) from blog.user_thumbs where userId=#{uid}")
  Integer getThumbNum(Long uid);


  List<Passage> selectOtherPassageByUserId(@Param("userId") Long userId);

  @Select("select content,title,summary,passageId,authorId from blog.passage where status=2 and isDelete=1 and isPrivate=1")
  List<Passage> selectPassageESData();

  void insertPassage(Passage passage);

  int updatePassage(Passage passage);

  @Update("update blog.passage set status=2 where passageId=#{passageId}")
  int publishPassage(Long passageId);

  //直接异或，相同为0，不同为1
  @Update("update blog.passage set isPrivate=isPrivate^1 where passageId=#{passageId}")
  boolean setPassagePrivate(Long passageId);

  @Select("select title,content,summary,thumbnail,passageId,status from blog.passage where passageId=#{passageId} and authorId=#{authorId}")
  Passage getEditPassageByPassageId(@Param("passageId") Long passageId,
      @Param("authorId") Long authorId);
}




