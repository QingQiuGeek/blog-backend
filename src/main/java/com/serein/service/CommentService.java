package com.serein.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.serein.model.dto.CommentDTO.CommentDTO;
import com.serein.model.entity.Comment;
import com.serein.model.vo.CommentVO.CommentVO;
import java.util.List;

/**
 * @author 懒大王Smile
 * @description 针对表【comment(评论表)】的数据库操作Service
 * @createDate 2024-09-12 22:19:13
 */
public interface CommentService extends IService<Comment> {

  Long commentPassage(CommentDTO commentDTO);

  List<CommentVO> getCommentByPassageId(Long authorId, Long passageId);

  Boolean deleteComment(Long commentId);


}
