package com.serein.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.serein.model.request.CommentRequest.AdminCommentPageRequest;
import com.serein.model.dto.CommentDTO.CommentDTO;
import com.serein.model.dto.CommentDTO.DeleteCommentDTO;
import com.serein.model.entity.Comment;
import com.serein.model.request.CommentRequest.CursorCommentRequest;
import com.serein.model.vo.CommentVO.CommentVO;
import java.util.List;
import java.util.Map;

/**
 * @author 懒大王Smile
 * @description 针对表【comment(评论表)】的数据库操作Service
 * @createDate 2024-09-12 22:19:13
 */
public interface CommentService extends IService<Comment> {

  Long commentPassage(CommentDTO commentDTO);

  Page<List<CommentVO>> getCommentByCursor(CursorCommentRequest cursorCommentRequest);

  Boolean deleteComment(DeleteCommentDTO deleteCommentDTO);


  Page<List<CommentVO>> getComments(AdminCommentPageRequest adminCommentPageRequest);
}
