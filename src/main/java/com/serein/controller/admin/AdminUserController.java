package com.serein.controller.admin;

import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.domain.dto.UserDTO;
import com.serein.exception.BusinessException;
import com.serein.service.UserService;
import com.serein.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "管理用户模块")
public class AdminUserController {

    @Autowired
    UserService userService;

    @ApiOperation(value = "禁用用户")
    @GetMapping("/disable/{userId}")
    public ResultUtils DisableUser(@PathVariable Long userId){
        return userService.disableUser(userId);
    }

    /**
     *
     * @param idList
     * @return
     */
    @ApiOperation(value = "根据id列表查询用户列表")
    @GetMapping("/getByIdList")
    public ResultUtils GetByIdList(@RequestParam List<Integer> idList){
        return userService.getByIdList(idList);
    }

    /**
     *
     * @param userId
     * @return
     */
    @ApiOperation(value = "删除用户")
    @DeleteMapping("/{userId}")
    public ResultUtils DeleteUserById(@PathVariable Long userId){
        if (userService.removeById(userId)){
            return ResultUtils.ok("删除用户成功");
        }
        throw new BusinessException(ErrorCode.UNEXPECTED_ERROR, ErrorInfo.DB_FAIL);
//        return ResultUtils.fail(ErrorCode.UNEXPECTED_ERROR, ErrorInfo.SYS_ERROR);
    }

    /**
     //todo 普通用户和管理员可以查询到的用户不同，根据isDelete字段 √
     *
     * @param current
     * @return
     */
    @ApiOperation(value = "分页获取用户列表")
    @GetMapping("/getUserList/{current}")
    public ResultUtils GetUserList(@PathVariable Long current){
        return userService.getUserList(current);
    }


    /**
     * // todo 添加用户 √
     * @param addUserDTO
     * @return
     */
    @ApiOperation(value = "添加用户")
    @PostMapping("/addUser")
    public ResultUtils AddUser(@RequestBody UserDTO addUserDTO){
        return userService.addUser(addUserDTO);
    }


    /**
     * //todo 管理修改用户 √
     * @param updateUserDTO
     * @return
     */
    @ApiOperation(value = "修改用户")
    @PostMapping("/updateUser")
    public ResultUtils UpdateUser(@RequestBody UserDTO updateUserDTO){
        return userService.updateUser(updateUserDTO);
    }




}
