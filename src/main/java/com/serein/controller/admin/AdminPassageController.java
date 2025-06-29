package com.serein.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serein.annotation.RoleCheck;
import com.serein.constants.UserRole;
import com.serein.model.request.PassageRequest.AdminPassageQueryPageRequest;
import com.serein.model.vo.passageVO.AdminPassageVO;
import com.serein.service.PassageService;
import com.serein.util.BR;
import com.serein.util.R;
import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:懒大王Smile
 * @Date: 2024/12/2
 * @Time: 18:27
 * @Description: 管理员 文章
 */

@RequestMapping("/admin/passage")
@RestController
public class AdminPassageController {

  @Resource
  private  PassageService passageService;

  /**
   * 拒绝/驳回文章
   *
   * @param passageId
   * @return
   */
//  @RoleCheck(mustRole = UserRole.ADMIN_ROLE)
  @GetMapping("/reject/{passageId}")
  public BR<Boolean> rejectPassage(@PathVariable String passageId) {
    return R.ok(passageService.rejectPassage(Long.valueOf(passageId)));
  }

  /**
   * 审核通过文章
   *
   * @param passageId
   * @return
   */
//  @RoleCheck(mustRole = UserRole.ADMIN_ROLE)
  @GetMapping("/publish/{passageId}")
  public BR<Boolean> publishPassage(@PathVariable String passageId) {
    return R.ok(passageService.publishPassage(Long.valueOf(passageId)));
  }

  /**
   * @param adminPassageQueryPageRequest
   * @return
   */
//  @RoleCheck(mustRole = UserRole.ADMIN_ROLE)
  @PostMapping("/getPassageList")
  public BR<Page<List<AdminPassageVO>>> getPassageList(
      @RequestBody AdminPassageQueryPageRequest adminPassageQueryPageRequest) {
    return R.ok(passageService.getPassageList(adminPassageQueryPageRequest));
  }


}
