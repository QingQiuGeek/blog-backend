package com.serein.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serein.annotation.RoleCheck;
import com.serein.constants.UserRole;
import com.serein.model.request.CommentRequest.AdminCommentPageRequest;
import com.serein.model.vo.commentVO.CommentVO;
import com.serein.service.CommentService;
import com.serein.util.BR;
import com.serein.util.R;
import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/12
 * @Time: 22:25
 * @Description: 管理员 评论
 */

@RestController
@RequestMapping("/admin/comment")
public class AdminCommentController {


  @Resource
  private CommentService commentService;

  /**
   * 获取文章评论
   * @param adminCommentPageRequest
   * @return
   */
//  @RoleCheck(mustRole = UserRole.ADMIN_ROLE)
  @PostMapping("/getComments/")
  public BR<Page<List<CommentVO>>> getComments(@RequestBody
  AdminCommentPageRequest adminCommentPageRequest) {
    return R.ok(commentService.getComments(adminCommentPageRequest));
  }

}
