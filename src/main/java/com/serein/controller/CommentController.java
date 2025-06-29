package com.serein.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serein.model.dto.commentDTO.CommentDTO;
import com.serein.model.dto.commentDTO.DeleteCommentDTO;
import com.serein.model.request.CommentRequest.CursorCommentRequest;
import com.serein.model.vo.commentVO.CommentVO;
import com.serein.service.CommentService;
import com.serein.util.BR;
import com.serein.util.R;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/12
 * @Time: 22:25
 * @Description: 评论Controller
 */

@RestController
@RequestMapping("/comment")
public class CommentController {


  @Resource
  private CommentService commentService;

  /*
   * 评论文章
   * */
  @PostMapping("")
  public BR<Long> commentPassage(@RequestBody CommentDTO commentDTO) {
    return R.ok(commentService.commentPassage(commentDTO));
  }


  /**
   * @param cursorCommentRequest
   * @return
   * @description 通过游标获取评论
   */
  @PostMapping("/getCommentByCursor")
  public BR<Page<List<CommentVO>>> getCommentByCursor(
      @RequestBody CursorCommentRequest cursorCommentRequest) {
    return R.ok(commentService.getCommentByCursor(cursorCommentRequest));
  }

  /*
   * 删除评论
   * */
  @PostMapping("/delete")
  public BR<Boolean> deleteComment(@RequestBody DeleteCommentDTO deleteCommentDTO) {
    return R.ok(commentService.deleteComment(deleteCommentDTO));
  }
}
