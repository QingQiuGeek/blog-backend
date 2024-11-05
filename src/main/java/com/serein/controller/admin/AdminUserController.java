package com.serein.controller.admin;

import com.serein.annotation.AuthCheck;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.constants.UserRole;
import com.serein.model.Request.GetUserByIdListRequest;
import com.serein.model.dto.userDTO.AddUserDTO;
import com.serein.exception.BusinessException;
import com.serein.model.enums.UserRoleEnum;
import com.serein.model.vo.UserVO.AdminUserVO;
import com.serein.model.vo.UserVO.UserVO;
import com.serein.service.UserService;
import com.serein.utils.BaseResponse;
import com.serein.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/19
 * @Time: 18:27
 * @Description:
 */

@RequestMapping("/admin")
@RestController
public class AdminUserController {

    @Autowired
    UserService userService;

    @AuthCheck(mustRole = UserRole.ADMIN_ROLE)
    @GetMapping("/disable/{userId}")
    public BaseResponse<Boolean> disableUser(@PathVariable Long userId){
        Boolean aBoolean = userService.disableUser(userId);
        return ResultUtils.success(aBoolean);
    }

    /**
     * 设置、取消管理员
     * @param userId
     * @return
     */
    @AuthCheck(mustRole = UserRole.ADMIN_ROLE)
    @GetMapping("/setAdmin/{userId}")
    public BaseResponse<Boolean> setAdmin(@PathVariable Long userId){
        Boolean aBoolean = userService.setAdmin(userId);
        return  ResultUtils.success(aBoolean);
    }


    /**
     * @param idList
     * @return
     */
    @AuthCheck(mustRole = UserRole.ADMIN_ROLE)
    @PostMapping("/getByIdList")
    public BaseResponse<List<AdminUserVO>> getByIdList(@RequestBody GetUserByIdListRequest idList){
        List<AdminUserVO> adminUserVOList = userService.getByIdList(idList.getIdList());
        return ResultUtils.success(adminUserVOList);
    }

    /**
     * @param userId
     * @return
     */
    @AuthCheck(mustRole = UserRole.ADMIN_ROLE)
    @DeleteMapping("/delete/{userId}")
    public BaseResponse<Boolean> deleteUserById(@PathVariable Long userId){

        if (userService.removeById(userId)){
            return ResultUtils.success(true);
        }
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.DB_FAIL);
    }

    /**
     * todo 分页查询优化
     * @param current
     * @return
     * 分页查询
     */
    @AuthCheck(mustRole = UserRole.ADMIN_ROLE)
    @GetMapping("/getUserList/{current}")
    public BaseResponse<List<AdminUserVO>> getUserList(@PathVariable Long current){
        List<AdminUserVO> adminUserVOList = userService.getUserList(current);
        return ResultUtils.success(adminUserVOList);
    }


    /**
     * @param addUserDTO
     * @return
     */
    @AuthCheck(mustRole = UserRole.ADMIN_ROLE)
    @PostMapping("/addUser")
    public BaseResponse<Long> addUser(@RequestBody AddUserDTO addUserDTO){
        Long userId = userService.addUser(addUserDTO);
        return ResultUtils.success(userId);
    }

}
