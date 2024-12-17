package com.serein.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.serein.model.QueryPageRequest;
import com.serein.model.dto.UserDTO.AddUserDTO;
import com.serein.model.dto.UserDTO.UpdateUserDTO;
import com.serein.model.entity.User;
import com.serein.model.request.UserRequest.AdminUserQueryPageRequest;
import com.serein.model.request.UserRequest.LoginRequest;
import com.serein.model.request.UserRequest.RegisterCodeRequest;
import com.serein.model.request.UserRequest.RegisterRequest;
import com.serein.model.vo.CommentVO.CommentVO;
import com.serein.model.vo.PassageVO.PassageInfoVO;
import com.serein.model.vo.UserVO.AdminUserVO;
import com.serein.model.vo.UserVO.LoginUserVO;
import com.serein.model.vo.UserVO.UserInfoDataVO;
import com.serein.model.vo.UserVO.UserVO;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

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

  Page<List<AdminUserVO>> getUserList(AdminUserQueryPageRequest adminUserQueryPageRequest);

  List<UserVO> getUserListByName(String userName);

  List<AdminUserVO> getByIdList(List<Long> idList);

  Boolean disableUser(Long userId);

  Boolean updateUser(UpdateUserDTO updateUserDTO);

  Long addUser(AddUserDTO addUserDTO);

  Page<List<PassageInfoVO>> myCollectPassage(QueryPageRequest queryPageRequest);

  Page<List<PassageInfoVO>> myThumbPassage(QueryPageRequest queryPageRequest);

  Page<List<PassageInfoVO>> myPassage(QueryPageRequest queryPageRequest);

  Boolean follow(Long userId);

  Page<List<UserVO>> myFollow(QueryPageRequest queryPageRequest);

  void sendRegisterCode(RegisterCodeRequest registerCodeRequest);

  Page<List<UserVO>> myFollowers(QueryPageRequest queryPageRequest);

  UserVO getUserInfo(Long uid);

  Boolean setAdmin(Long userId);

  UserInfoDataVO getUserInfoData();

  Page<List<CommentVO>> myMessage(QueryPageRequest queryPageRequest);
}
