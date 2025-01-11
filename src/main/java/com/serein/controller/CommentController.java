package com.serein.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serein.model.dto.commentDTO.CommentDTO;
import com.serein.model.dto.commentDTO.DeleteCommentDTO;
import com.serein.model.request.CommentRequest.CursorCommentRequest;
import com.serein.model.vo.commentVO.CommentVO;
import com.serein.service.CommentService;
import com.serein.util.BaseResponse;
import com.serein.util.ResultUtil;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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


  @Resource
  private  CommentService commentService;

  /*
   * 评论文章
   * */
  @PostMapping("")
  public BaseResponse<Long> commentPassage(@RequestBody CommentDTO commentDTO) {
    Long cid = commentService.commentPassage(commentDTO);
    return ResultUtil.success(cid);
  }


  /**
   * @param cursorCommentRequest
   * @return
   * @description 通过游标获取评论
   */
  @PostMapping("/getCommentByCursor")
//  BaseResponse<Map<Long,List<CommentVO>>>
  public BaseResponse<Page<List<CommentVO>>> getCommentByCursor(
      @RequestBody CursorCommentRequest cursorCommentRequest) {
    Page<List<CommentVO>> commentVOList = commentService.getCommentByCursor(cursorCommentRequest);
    return ResultUtil.success(commentVOList);
  }

  /*
   * 删除评论
   * */
  @PostMapping("/delete")
  public BaseResponse<Boolean> deleteComment(@RequestBody DeleteCommentDTO deleteCommentDTO) {
    Boolean aBoolean = commentService.deleteComment(deleteCommentDTO);
    return ResultUtil.success(aBoolean);
  }
}
