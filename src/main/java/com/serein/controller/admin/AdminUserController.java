package com.serein.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serein.annotation.RoleCheck;
import com.serein.constants.UserRole;
import com.serein.model.dto.userDTO.AddUserDTO;
import com.serein.model.request.UserRequest.AdminUserQueryPageRequest;
import com.serein.model.request.UserRequest.GetUserByIdListRequest;
import com.serein.model.vo.userVO.AdminUserVO;
import com.serein.service.UserService;
import com.serein.util.BR;
import com.serein.util.R;
import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/19
 * @Time: 18:27
 * @Description: 管理员 用户
 */

@RequestMapping("/admin/user")
@RestController
public class AdminUserController {

  @Resource
  private UserService userService;

  /**
   * 禁用、解除禁用用户
   *
   * @param userId
   * @return
   */
//  @RoleCheck(mustRole = UserRole.ADMIN_ROLE)
  @GetMapping("/disable/{userId}")
  public BR<Boolean> banUser(@PathVariable Long userId) {
    return R.ok(userService.banUser(userId));
  }


  /**
   * @param idList
   * @return
   */
//  @RoleCheck(mustRole = UserRole.ADMIN_ROLE)
  @PostMapping("/getByIdList")
  public BR<List<AdminUserVO>> getByIdList(@RequestBody GetUserByIdListRequest idList) {
    return R.ok(userService.getByIdList(idList.getIdList()));
  }

  /**
   * 删除用户
   *
   * @param userId
   * @return
   */
//  @RoleCheck(mustRole = UserRole.ADMIN_ROLE)
  @DeleteMapping("/delete/{userId}")
  public BR<Boolean> deleteUserById(@PathVariable Long userId) {
      return R.ok(userService.removeById(userId));
  }

  /**
   * @param adminUserQueryPageRequest
   * @return
   */
//  @RoleCheck(mustRole = UserRole.ADMIN_ROLE)
  @PostMapping("/getUserList")
  public BR<Page<List<AdminUserVO>>> getUserList(
      @RequestBody AdminUserQueryPageRequest adminUserQueryPageRequest) {
    return R.ok(userService.getUserList(adminUserQueryPageRequest));
  }


  /**
   * @param addUserDTO
   * @return
   */
//  @RoleCheck(mustRole = UserRole.ADMIN_ROLE)
  @PostMapping("/addUser")
  public BR<Long> addUser(@RequestBody AddUserDTO addUserDTO) {
    return R.ok(userService.addUser(addUserDTO));
  }

}
