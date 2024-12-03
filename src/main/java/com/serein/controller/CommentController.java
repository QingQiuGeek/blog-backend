package com.serein.controller;

import com.serein.model.dto.CommentDTO.CommentDTO;
import com.serein.model.vo.CommentVO.CommentVO;
import com.serein.service.CommentService;
import com.serein.util.BaseResponse;
import com.serein.util.ResultUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/12
 * @Time: 22:25
 * @Description:
 */

@RestController
@RequestMapping("/comment")
public class CommentController {


  @Autowired
  CommentService commentService;

  /*
   * 评论文章
   * */
  @PostMapping("")
  public BaseResponse<Long> commentPassage(@RequestBody CommentDTO commentDTO) {
    Long cid = commentService.commentPassage(commentDTO);
    return ResultUtil.success(cid);
  }

  /*
   * 获取文章评论
   * */
  @GetMapping("/{authorId}/{passageId}")
  public BaseResponse<List<CommentVO>> getCommentByPassageId(@PathVariable Long authorId,
      @PathVariable String passageId) {
    List<CommentVO> commentVOList = commentService.getCommentByPassageId(authorId,
        Long.valueOf(passageId));
    return ResultUtil.success(commentVOList);
  }

  /*
   * 删除评论
   * */
  @PutMapping("/delete/{commentId}")
  public BaseResponse<Boolean> deleteComment(@PathVariable Long commentId) {
    Boolean aBoolean = commentService.deleteComment(commentId);
    return ResultUtil.success(aBoolean);
  }
}
