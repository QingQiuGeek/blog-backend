package com.serein.controller.admin;

import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.model.Request.GetUserByIdListRequest;
import com.serein.model.dto.userDTO.AddUserDTO;
import com.serein.exception.BusinessException;
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

    @GetMapping("/disable/{userId}")
    public Boolean disableUser(@PathVariable Long userId){
        return userService.disableUser(userId);
    }

    /**
     * @param idList
     * @return
     */
    @PostMapping("/getByIdList")
    public BaseResponse<List<UserVO>> getByIdList(@RequestBody GetUserByIdListRequest idList){
        List<UserVO> adminUserVOList = userService.getByIdList(idList.getIdList());
        return ResultUtils.success(adminUserVOList);
    }

    /**
     *
     * @param userId
     * @return
     */
    @DeleteMapping("/delete/{userId}")
    public Boolean deleteUserById(@PathVariable Long userId){
        if (userService.removeById(userId)){
            return true;
        }
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.DB_FAIL);
    }

    /**
     * todo 分页查询优化
     * @param current
     * @return
     * 分页查询
     */
    @GetMapping("/getUserList/{current}")
    public BaseResponse<List<UserVO>> getUserList(@PathVariable Long current){
        List<UserVO> userList = userService.getUserList(current);
        return ResultUtils.success(userList);
    }


    /**
     * @param addUserDTO
     * @return
     */
    @PostMapping("/addUser")
    public BaseResponse<Long> addUser(@RequestBody AddUserDTO addUserDTO){
        Long userId = userService.addUser(addUserDTO);
        return ResultUtils.success(userId);
    }






}
