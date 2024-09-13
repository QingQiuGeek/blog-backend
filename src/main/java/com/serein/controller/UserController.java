package com.serein.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.serein.domain.dto.LoginUserDTO;
import com.serein.domain.entity.User;
import com.serein.service.UserService;
import com.serein.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/12
 * @Time: 22:23
 * @Description:
 */

@Api(value = "用户模块")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    /**
     *
     * @param loginUserDto
     * @param httpServletRequest
     * @return
     */
    @ApiOperation(value = "登录")
    @PostMapping("login")
    public ResultUtils Login(@RequestBody LoginUserDTO loginUserDto, HttpServletRequest httpServletRequest){
        //todo 先使用session登录，后改成redis
        return userService.login(loginUserDto,httpServletRequest);
    }

    /**
     *
     * @param loginUserDTO
     * @param httpServletRequest
     * @return
     */
    @ApiOperation(value = "注册")
    @PostMapping("register")
    public ResultUtils Register(@RequestBody LoginUserDTO loginUserDTO, HttpServletRequest httpServletRequest){
        //todo 前期使用 用户名密码即可注册，用户名不可重复
        return userService.register(loginUserDTO,httpServletRequest);
    }

    /**
     *
     * @param httpServletRequest
     * @return
     */
    @ApiOperation(value = "退出登录")
    @PostMapping("logout")
    public ResultUtils Logout(HttpServletRequest httpServletRequest){
        return userService.logout(httpServletRequest);
    }

    /**
     *
     * @param userName
     * @return
     */
    @ApiOperation(value = "根据用户名查询用户")
    @GetMapping("{userName}")
    public ResultUtils GetByUserName(@RequestParam String userName){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName",userName);
        return ResultUtils.ok("根据用户名查询用户成功",userService.list(queryWrapper));
    }

    @ApiOperation(value = "根据id查询用户列表")
    @GetMapping("getByIdList")
    public ResultUtils GetByUserName(@RequestBody List<Long> idList){
        return ResultUtils.ok("根据id列表查询用户成功",userService.listByIds(idList));
    }

//    @ApiOperation(value = "获取登录用户")
//    @GetMapping("/getLoginUser")
//    public ResultUtils GetLoginUSer(){
//
//    }
}
