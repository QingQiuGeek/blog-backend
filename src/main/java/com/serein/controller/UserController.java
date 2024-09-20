package com.serein.controller;


import com.serein.domain.Request.LoginRequest;
import com.serein.domain.Request.RegisterRequest;
import com.serein.domain.dto.UserDTO;
import com.serein.service.UserService;
import com.serein.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * @Author:懒大王Smile
 * @Date: 2024/9/12
 * @Time: 22:23
 * @Description:
 */

@Api(tags = "用户模块")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    /**
     *
     * @param loginRequest
     * @param httpServletRequest
     * @return
     */
    @ApiOperation(value = "登录")
    @PostMapping("/login")
    public ResultUtils Login(@RequestBody LoginRequest loginRequest, HttpServletRequest httpServletRequest){
        return userService.login(loginRequest,httpServletRequest);
    }

    /**
     *
     * @param registerRequest
     * @param httpServletRequest
     */
    @ApiOperation(value = "注册")
    @PostMapping("/register")
    public ResultUtils Register(@RequestBody RegisterRequest registerRequest, HttpServletRequest httpServletRequest){
        //todo 前期使用 userAccount和密码即可注册，userAccount不可重复
        return userService.register(registerRequest,httpServletRequest);
    }

    /**
     *
     * @param httpServletRequest
     * @return
     */
    @ApiOperation(value = "退出登录")
    @PostMapping("/logout")
    public ResultUtils Logout(HttpServletRequest httpServletRequest){
        return userService.logout(httpServletRequest);
    }

    /**
     *
     * @param userName
     * @return
     */
    @ApiOperation(value = "根据用户名查询用户")
    @GetMapping("/{userName}")
    public ResultUtils GetByUserName(@PathVariable String userName){
        return userService.getByUserName(userName);
    }


    /**
     *
     * @return
     */
    @ApiOperation(value = "获取登录用户")
    @GetMapping("/getLoginUser")
    public ResultUtils GetLoginUSer(){
        return ResultUtils.ok("获取登陆用户成功",userService.getLoginUser());
    }

    /**
     * //todo 普通用户修改用户，不能修改用户status，admin可以
     * 前端普通用户信息不显示role、status，不传该字段
     * @param updateUserDTO
     * @return
     */
    @ApiOperation(value = "修改用户")
    @GetMapping("/updateUser")
    public ResultUtils UpdateUser(@RequestBody UserDTO updateUserDTO){
        return userService.updateUser(updateUserDTO);
    }

}
