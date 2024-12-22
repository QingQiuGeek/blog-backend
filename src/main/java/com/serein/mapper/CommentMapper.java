package com.serein.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serein.model.entity.Comment;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 懒大王Smile
 * @description 针对表【comment(评论表)】的数据库操作Mapper
 * @createDate 2024-09-12 22:19:13
 * @Entity com.serein.domain.entity.Comment
 */

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {


  void insertComment(Comment comment);


  List<Comment> selectCommentsByCursor(@Param("passageId") Long passageId,
      @Param("authorId") Long authorId, @Param("pageSize") Integer pageSize,
      @Param("lastCommentId") Long lastCommentId);

  @Delete("delete from blog.comment where passageId=#{passageId}")
  void deleteByPassageId(Long passageId);

}




