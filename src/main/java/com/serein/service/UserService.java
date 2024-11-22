package com.serein.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.serein.model.Request.LoginRequest;
import com.serein.model.Request.RegisterCodeRequest;
import com.serein.model.Request.RegisterRequest;
import com.serein.model.dto.userDTO.UpdateUserDTO;
import com.serein.model.dto.userDTO.AddUserDTO;
import com.serein.model.entity.User;
import com.serein.model.vo.CommentVO.CommentVO;
import com.serein.model.vo.PassageVO.PassageInfoVO;
import com.serein.model.vo.PassageVO.PassageVO;
import com.serein.model.vo.UserVO.AdminUserVO;
import com.serein.model.vo.UserVO.LoginUserVO;
import com.serein.model.vo.UserVO.UserInfoDataVO;
import com.serein.model.vo.UserVO.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 懒大王Smile
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-09-12 22:19:13
*/
public interface UserService extends IService<User> {



    LoginUserVO login(LoginRequest loginRequest);

    LoginUserVO register(RegisterRequest registerRequest);

    Boolean logout(HttpServletRequest httpServletRequest);

    LoginUserVO getLoginUser();

    List<AdminUserVO> getUserList(Long current);

    List<UserVO> getUserListByName(String userName);

    List<AdminUserVO>getByIdList(List<Long> idList);

    Boolean disableUser(Long userId);

    Boolean updateUser(UpdateUserDTO updateUserDTO);

    Long addUser(AddUserDTO addUserDTO);

    List<PassageInfoVO> myCollectPassage();

    List<PassageInfoVO> myThumbPassage();

    List<PassageInfoVO> myPassage();

    Boolean follow(Long userId);

    List<UserVO> myFollow();

   void  sendRegisterCode(RegisterCodeRequest registerCodeRequest);

    List<UserVO> myFollowers();

    UserVO getUserInfo(Long uid);

    Boolean setAdmin(Long userId);

    UserInfoDataVO getUserInfoData();

    List<CommentVO> myMessage();
}
