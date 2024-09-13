package com.serein.service;

import com.serein.domain.dto.LoginUserDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.serein.domain.entity.User;
import com.serein.utils.ResultUtils;

import javax.servlet.http.HttpServletRequest;

/**
* @author 懒大王Smile
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-09-12 22:19:13
*/
public interface UserService extends IService<User> {

    ResultUtils login(LoginUserDTO loginUserDto, HttpServletRequest httpServletRequest);

    ResultUtils register(LoginUserDTO loginUserDTO, HttpServletRequest httpServletRequest);

    ResultUtils logout(HttpServletRequest httpServletRequest);
}
