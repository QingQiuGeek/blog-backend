package com.serein.service;

import com.serein.domain.Request.LoginRequest;
import com.serein.domain.Request.RegisterRequest;
import com.serein.domain.dto.LoginUserDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.serein.domain.dto.UserDTO;
import com.serein.domain.entity.User;
import com.serein.utils.ResultUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 懒大王Smile
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-09-12 22:19:13
*/
public interface UserService extends IService<User> {




    ResultUtils login(LoginRequest loginRequest, HttpServletRequest httpServletRequest);

    ResultUtils register(RegisterRequest registerRequest, HttpServletRequest httpServletRequest);

    ResultUtils logout(HttpServletRequest httpServletRequest);

    LoginUserDTO getLoginUser();

    ResultUtils getUserList(Long current);

    ResultUtils getByUserName(String userName);

    ResultUtils getByIdList(List<Integer> idList);

    ResultUtils disableUser(Long userId);

    ResultUtils updateUser(UserDTO updateUserDTO);

    ResultUtils addUser(UserDTO addUserDTO);
}
