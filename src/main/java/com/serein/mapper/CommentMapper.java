package com.serein.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serein.model.entity.Comment;
import com.serein.model.vo.CommentVO.CommentVO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author 懒大王Smile
 * @description 针对表【comment(评论表)】的数据库操作Mapper
 * @createDate 2024-09-12 22:19:13
 * @Entity com.serein.domain.entity.Comment
 */

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

  @Select("select * from blog.comment where passageId=#{passageId} and isDelete=1 ORDER BY commentTime DESC")
  List<CommentVO> getCommentVoListByPassageId(Long passageId);


  void insertComment(Comment comment);

  @Select("select * from blog.comment where authorId=#{userId} and isDelete=1 ORDER BY commentTime DESC")
  List<CommentVO> getCommentVoListByAuthorId(Long userId);
}




