package com.serein.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serein.annotation.AuthCheck;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.constants.UserRole;
import com.serein.model.AdminUserQueryPageRequest;
import com.serein.model.Request.GetUserByIdListRequest;
import com.serein.model.dto.userDTO.AddUserDTO;
import com.serein.exception.BusinessException;
import com.serein.model.vo.UserVO.AdminUserVO;
import com.serein.service.UserService;
import com.serein.utils.BaseResponse;
import com.serein.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/19
 * @Time: 18:27
 * @Description:
 */

@RequestMapping("/admin/user")
@RestController
public class AdminUserController {

    @Autowired
    UserService userService;

    /**
     * 禁用、解除禁用用户
     * @param userId
     * @return
     */
    @AuthCheck(mustRole = UserRole.ADMIN_ROLE)
    @GetMapping("/disable/{userId}")
    public BaseResponse<Boolean> disableUser(@PathVariable Long userId){
        Boolean aBoolean = userService.disableUser(userId);
        return ResultUtil.success(aBoolean);
    }


    /**
     * @param idList
     * @return
     */
    @AuthCheck(mustRole = UserRole.ADMIN_ROLE)
    @PostMapping("/getByIdList")
    public BaseResponse<List<AdminUserVO>> getByIdList(@RequestBody GetUserByIdListRequest idList){
        List<AdminUserVO> adminUserVOList = userService.getByIdList(idList.getIdList());
        return ResultUtil.success(adminUserVOList);
    }

    /**
     * 删除用户
     * @param userId
     * @return
     */
    @AuthCheck(mustRole = UserRole.ADMIN_ROLE)
    @DeleteMapping("/delete/{userId}")
    public BaseResponse<Boolean> deleteUserById(@PathVariable Long userId){

        if (userService.removeById(userId)){
            return ResultUtil.success(true);
        }
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.DB_FAIL);
    }

    /**
     *
     * @param adminUserQueryPageRequest
     * @return
     */
    @AuthCheck(mustRole = UserRole.ADMIN_ROLE)
    @PostMapping("/getUserList")
    public BaseResponse<Page<List<AdminUserVO>>> getUserList(@RequestBody AdminUserQueryPageRequest adminUserQueryPageRequest){
        Page<List<AdminUserVO>> adminUserVOList = userService.getUserList(adminUserQueryPageRequest);
        return ResultUtil.success(adminUserVOList);
    }


    /**
     * @param addUserDTO
     * @return
     */
    @AuthCheck(mustRole = UserRole.ADMIN_ROLE)
    @PostMapping("/addUser")
    public BaseResponse<Long> addUser(@RequestBody AddUserDTO addUserDTO){
        Long userId = userService.addUser(addUserDTO);
        return ResultUtil.success(userId);
    }

}
