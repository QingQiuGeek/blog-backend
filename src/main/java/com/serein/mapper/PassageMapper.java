package com.serein.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serein.model.entity.Passage;
import com.serein.model.vo.PassageVO.PassageVO;
import org.apache.ibatis.annotations.Mapper;
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

    void updateViewNum(Long passageId);

    @Select("select content,authorId from blog.passage where passageId=#{pid}")
    PassageVO getPassageContentByPid(Long pid);

    @Select("select  avatarUrl from blog.user where userId =#{authorId}")
    String getAuthorAvatar(Long authorId);
}




