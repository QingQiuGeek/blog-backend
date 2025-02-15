package com.serein.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serein.annotation.AuthCheck;
import com.serein.constants.UserRole;
import com.serein.model.request.CommentRequest.AdminCommentPageRequest;
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
@RequestMapping("/admin/comment")
public class AdminCommentController {


  @Resource
  private CommentService commentService;

  /*
   * 获取文章评论
   * */
  @AuthCheck(mustRole = UserRole.ADMIN_ROLE)
  @PostMapping("/getComments/")
  public BaseResponse<Page<List<CommentVO>>> getComments(@RequestBody
  AdminCommentPageRequest adminCommentPageRequest) {
    Page<List<CommentVO>> commentVOList = commentService.getComments(adminCommentPageRequest);
    return ResultUtil.success(commentVOList);
  }

}
