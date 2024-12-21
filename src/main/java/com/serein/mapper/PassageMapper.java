package com.serein.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serein.model.entity.Passage;
import com.serein.model.vo.PassageVO.PassageContentVO;
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

  @Select("select content from blog.passage where passageId=#{pid} and authorId=#{uid}")
  PassageContentVO getPassageContentByPid(@Param("uid") Long uid, @Param("pid") Long pid);


  @Select("select passageId,authorId,collectNum,viewNum,commentNum,thumbNum,accessTime,title,summary from blog.passage where passageId=#{passageId}")
  Passage getPassageInfo(Long passageId);

  @Select("select count(*) from blog.user_thumbs where userId=#{uid}")
  Integer getThumbNum(Long uid);

  @Update("update blog.passage set commentNum=commentNum+1 where passageId=#{passageId}")
  Boolean addCommentNum(Long passageId);

  @Update("update blog.passage set commentNum=commentNum-1 where passageId=#{passageId}")
  Boolean subCommentNum(Long passageId);

  @Update("update blog.passage set collectNum=collectNum+1 where passageId=#{passageId}")
  Boolean addCollectNum(Long passageId);

  @Update("update blog.passage set collectNum=collectNum-1 where passageId=#{passageId}")
  boolean subCollectNum(Long passageId);

  @Update("update blog.passage set thumbNum=thumbNum+1 where passageId=#{passageId}")
  boolean addThumbNum(Long passageId);

  @Update("update blog.passage set thumbNum=thumbNum-1 where passageId=#{passageId}")
  boolean subThumbNum(Long passageId);

  List<Passage> selectOtherPassageByUserId(@Param("userId") Long userId);

  @Select("select content,title,summary,passageId from blog.passage where status=2 and isDelete=1")
  List<Passage> selectPassageESData();

  void insertPassage(Passage passage);

  int updatePassage(Passage passage);

  @Update("update blog.passage set status=2 where passageId=#{passageId}")
  int publishPassage(Long passageId);
}




