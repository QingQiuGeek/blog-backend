package com.serein.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serein.model.entity.Passage;
import com.serein.model.vo.PassageVO.PassageContentVO;
import com.serein.model.vo.PassageVO.PassageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
* @author 懒大王Smile
* @description 针对表【passage(文章表)】的数据库操作Mapper
* @createDate 2024-09-12 22:19:13
* @Entity com.serein.domain.entity.Passage
*/
@Mapper
public interface PassageMapper extends BaseMapper<Passage> {

    List<Passage> listPassageWithNODelete(Date minutesAgoDate);

    //根据uid查询该用户收藏的文章数量
    @Select("select count(*) from blog.user_collects where userId=#{uid}")
    Integer getCollectNumById(Long uid);

    @Select("select count(*) from blog.passage where authorId=#{uid}")
    Integer getPassageNumById(Long uid);

    void updateViewNum(Long passageId);

    @Select("select content from blog.passage where passageId=#{pid} and authorId=#{uid}")
    PassageContentVO getPassageContentByPid(@Param("uid") Long uid, @Param("pid")Long pid);

    @Select("select  avatarUrl from blog.user where userId =#{authorId}")
    String getAuthorAvatar(Long authorId);


    @Select("select passageId,collectNum,viewNum,commentNum,thumbNum,accessTime,title,summary,pTags from blog.passage where passageId=#{passageId}")
    Passage getPassageInfo(Long passageId);


    @Select("select count(*) from blog.user_thumbs where userId=#{uid}")
    Integer getThumbNum(Long uid);
}




