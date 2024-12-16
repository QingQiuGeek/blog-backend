package com.serein.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serein.model.request.CommentRequest.AdminCommentPageRequest;
import com.serein.model.vo.CommentVO.CommentVO;
import com.serein.service.CommentService;
import com.serein.util.BaseResponse;
import com.serein.util.ResultUtil;
import java.util.List;
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
@RequestMapping("/admin/comment")
public class AdminCommentController {


  @Autowired
  CommentService commentService;

  /*
   * 获取文章评论
   * */
  @PostMapping("/getComments/")
  public BaseResponse<Page<List<CommentVO>>> getComments(@RequestBody
      AdminCommentPageRequest adminCommentPageRequest) {
    Page<List<CommentVO>> commentVOList = commentService.getComments(adminCommentPageRequest);
    return ResultUtil.success(commentVOList);
  }

}
