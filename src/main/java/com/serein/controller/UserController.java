package com.serein.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serein.model.QueryPageRequest;
import com.serein.model.dto.UserDTO.UpdateUserDTO;
import com.serein.model.request.UserRequest.LoginRequest;
import com.serein.model.request.UserRequest.RegisterCodeRequest;
import com.serein.model.request.UserRequest.RegisterRequest;
import com.serein.model.vo.CommentVO.CommentVO;
import com.serein.model.vo.PassageVO.PassageInfoVO;
import com.serein.model.vo.UserVO.LoginUserVO;
import com.serein.model.vo.UserVO.UserInfoDataVO;
import com.serein.model.vo.UserVO.UserVO;
import com.serein.service.PassageService;
import com.serein.service.UserService;
import com.serein.util.BaseResponse;
import com.serein.util.ResultUtil;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author:懒大王Smile
 * @Date: 2024/9/12
 * @Time: 22:23
 * @Description:
 */

@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired
  PassageService passageService;
  @Autowired
  UserService userService;


  /**
   * 用户登录
   *
   * @param loginRequest
   * @return
   */
  @PostMapping("/login")
  public BaseResponse<LoginUserVO> login(@RequestBody LoginRequest loginRequest) {
//        int a=1/0;
    LoginUserVO loginUserVO = userService.login(loginRequest);
    return ResultUtil.success(loginUserVO);
  }

  /**
   * 用户注册
   *
   * @param registerRequest
   */
  @PostMapping("/register")
  public BaseResponse<LoginUserVO> register(@RequestBody RegisterRequest registerRequest) {
    LoginUserVO loginUserVO = userService.register(registerRequest);
    return ResultUtil.success(loginUserVO);
  }

  /**
   * 退出登录
   *
   * @return
   */
  @PostMapping("/logout")
  public BaseResponse<Boolean> logout(HttpServletRequest httpServletRequest) {
    Boolean logout = userService.logout(httpServletRequest);
    return ResultUtil.success(logout);
  }

  /*
   * 获取个人主页展示的粉丝数量、文章收藏量、作品数量、关注数量、点赞数量
   * */
  @GetMapping("/userInfoData")
  public BaseResponse<UserInfoDataVO> getUserInfoData() {
    UserInfoDataVO userInfoData = userService.getUserInfoData();
    return ResultUtil.success(userInfoData);
  }

  /**
   * 根据用户名查询用户列表 用户名可以重复
   *
   * @param userName
   * @return
   */
  @GetMapping("/find/{userName}")
  public BaseResponse<List<UserVO>> getUserListByName(@PathVariable String userName) {
    List<UserVO> userVOList = userService.getUserListByName(userName);
    return ResultUtil.success(userVOList);
  }


  /**
   * 获取当前登录用户
   *
   * @return
   */
  @GetMapping("/getLoginUser")
  public BaseResponse<LoginUserVO> getLoginUser() {
    LoginUserVO loginUserVO = userService.getLoginUser();
    return ResultUtil.success(loginUserVO);
  }

  /**
   * 用户可以更新自己的个人信息
   *
   * @param updateUserDTO
   * @return
   */
  @PostMapping("/updateUser")
  public BaseResponse<Boolean> updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
    Boolean aBoolean = userService.updateUser(updateUserDTO);
    return ResultUtil.success(aBoolean);
  }

  /*
   * 关注用户
   * */
  @PutMapping("/follow/{userId}")
  public BaseResponse<Boolean> follow(@PathVariable Long userId) {
    Boolean follow = userService.follow(userId);
    return ResultUtil.success(follow);
  }

  /**
   * 根据uid获取用户的信息，一般用于查询用户主页或者文章作者信息
   * TODO 该方法和 getUserInfoData重复
   *
   * @return
   */
  @GetMapping("/getUserInfo/{uid}")
  public BaseResponse<UserVO> getUserInfo(@PathVariable Long uid) {
    UserVO userInfo = userService.getUserInfo(uid);
    return ResultUtil.success(userInfo);
  }

  /*
   * 我的粉丝，list.size是粉丝数量
   * */
  @PostMapping("/myFollowers")
  public BaseResponse<Page<List<UserVO>>> myFollowers(
      @RequestBody QueryPageRequest queryPageRequest) {
    Page<List<UserVO>> userVOS = userService.myFollowers(queryPageRequest);
    return ResultUtil.success(userVOS);
  }

  /*
   * 根据userId查询粉丝数量，展示在文章详情页的作者介绍上
   * */
//    @GetMapping("/followerNum/{uid}")
//    public BaseResponse<Integer> getFollowerNum(@PathVariable Long uid){
//        int followerNum=userService.getFollowerNum(uid);
//        return ResultUtils.success(followerNum);
//    }


  /*
   * 我关注的用户
   * list.size是数量
   * */
  @PostMapping("/myFollow")
  public BaseResponse<Page<List<UserVO>>> myFollow(@RequestBody QueryPageRequest queryPageRequest) {
    Page<List<UserVO>> userVOS = userService.myFollow(queryPageRequest);
    return ResultUtil.success(userVOS);
  }

  @PostMapping("/sendRegisterCode")
  public BaseResponse<Boolean> sendRegisterCode(
      @RequestBody RegisterCodeRequest registerCodeRequest) {
    userService.sendRegisterCode(registerCodeRequest);
    return ResultUtil.success(true);
  }

  /*
   * 我的收藏博客列表
   * */
  @PostMapping("/myCollect")
  public BaseResponse<Page<List<PassageInfoVO>>> myCollectPassage(
      @RequestBody QueryPageRequest queryPageRequest) {
    Page<List<PassageInfoVO>> passageVOList = userService.myCollectPassage(queryPageRequest);
    return ResultUtil.success(passageVOList);
  }

  /*
   * 我的点赞博客列表
   * */
  @PostMapping("/myThumb")
  public BaseResponse<Page<List<PassageInfoVO>>> myThumbPassage(
      @RequestBody QueryPageRequest queryPageRequest) {
    Page<List<PassageInfoVO>> passageVOList = userService.myThumbPassage(queryPageRequest);
    return ResultUtil.success(passageVOList);
  }

  /*
   * 我的文章，list.size即为我的文章数量
   * */
  @PostMapping("/myPassages")
  public BaseResponse<Page<List<PassageInfoVO>>> myPassages(
      @RequestBody QueryPageRequest queryPageRequest) {
    Page<List<PassageInfoVO>> passageVOList = userService.myPassage(queryPageRequest);
    return ResultUtil.success(passageVOList);
  }

  /*
   * 我的消息，用户对我的文章的评论即为我的消息
   * */
  @PostMapping("/myMessage")
  public BaseResponse<Page<List<CommentVO>>> myMessage(
      @RequestBody QueryPageRequest queryPageRequest) {
    Page<List<CommentVO>> commentVOList = userService.myMessage(queryPageRequest);
    return ResultUtil.success(commentVOList);
  }


}
