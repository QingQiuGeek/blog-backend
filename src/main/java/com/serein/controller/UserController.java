package com.serein.controller;


import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serein.model.request.QueryPageRequest;
import com.serein.model.dto.userDTO.UpdateUserDTO;
import com.serein.model.request.UserRequest.LoginRequest;
import com.serein.model.request.UserRequest.RegisterCodeRequest;
import com.serein.model.request.UserRequest.RegisterRequest;
import com.serein.model.vo.commentVO.CommentVO;
import com.serein.model.vo.passageVO.PassageInfoVO;
import com.serein.model.vo.userVO.LoginUserVO;
import com.serein.model.vo.userVO.UserInfoDataVO;
import com.serein.model.vo.userVO.UserVO;
import com.serein.service.PassageService;
import com.serein.service.UserService;
import com.serein.util.BR;
import com.serein.util.R;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


/**
 * @Author:懒大王Smile
 * @Date: 2024/9/12
 * @Time: 22:23
 * @Description: 用户Controller
 */

@RestController
@RequestMapping("/user")
public class UserController {

  @Resource
  private UserService userService;


  /**
   * 用户登录
   *
   * @param loginRequest
   * @return
   */
  @PostMapping("/login")
  public BR<LoginUserVO> login(@RequestBody LoginRequest loginRequest) {
    return R.ok(userService.login(loginRequest));
  }

  /**
   * 用户注册
   *
   * @param registerRequest
   */
  @PostMapping("/register")
  public BR<LoginUserVO> register(@RequestBody RegisterRequest registerRequest) {
    return R.ok(userService.register(registerRequest));
  }

  /**
   * 退出登录
   *
   * @return
   */
  @PostMapping("/logout")
  public BR<Boolean> logout(HttpServletRequest httpServletRequest) {
    return R.ok(userService.logout(httpServletRequest));
  }

  /*
   * 获取个人主页展示的粉丝数量、文章收藏量、作品数量、关注数量、点赞数量
   * */
  @GetMapping("/userInfoData")
  public BR<UserInfoDataVO> getUserInfoData() {
    return R.ok(userService.getUserInfoData());
  }

  /**
   * 根据用户名查询用户列表 用户名可以重复
   *
   * @param userName
   * @return
   */
  @GetMapping("/find/{userName}")
  public BR<List<UserVO>> getUserListByName(@PathVariable("userName") String userName) {
    return R.ok(userService.getUserListByName(userName));
  }


  /**
   * 获取当前登录用户
   *
   * @return
   */
  @GetMapping("/getLoginUser")
  public BR<LoginUserVO> getLoginUser() {
    return R.ok(userService.getLoginUser());
  }

  /**
   * 用户可以更新自己的个人信息
   *
   * @param updateUserDTO
   * @return
   */
  @PostMapping("/updateUser")
  public BR<Boolean> updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
    return R.ok( userService.updateUser(updateUserDTO));
  }

  @PostMapping("/uploadAvatar")
  public BR<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
    return R.ok(userService.uploadAvatar(file));
  }


  /*
   * 关注用户
   * */
  @PutMapping("/follow/{userId}")
  public BR<Boolean> follow(@PathVariable("userId") Long userId) {
    return R.ok(userService.follow(userId));
  }

  /**
   * 用于查询文章作者信息
   * @return
   */
  @GetMapping("/getUserInfo/{uid}")
  public BR<UserVO> getUserInfo(@PathVariable("uid") Long uid) {
    return R.ok( userService.getUserInfo(uid));
  }

  /*
   * 我的粉丝，list.size是粉丝数量
   * */
  @PostMapping("/myFollowers")
  public BR<Page<List<UserVO>>> myFollowers(
      @RequestBody QueryPageRequest queryPageRequest) {
    return R.ok(userService.myFollowers(queryPageRequest));
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
  public BR<Page<List<UserVO>>> myFollow(@RequestBody QueryPageRequest queryPageRequest) {
    return R.ok( userService.myFollow(queryPageRequest));
  }

  @PostMapping("/sendRegisterCode")
  public BR<Boolean> sendRegisterCode(
      @RequestBody RegisterCodeRequest registerCodeRequest) {
    return R.ok(userService.sendRegisterCode(registerCodeRequest));
  }

  /*
   * 我的收藏博客列表
   * */
  @PostMapping("/myCollect")
  public BR<Page<List<PassageInfoVO>>> myCollectPassage(
      @RequestBody QueryPageRequest queryPageRequest) {
    return R.ok(userService.myCollectPassage(queryPageRequest));
  }

  /*
   * 我的点赞博客列表
   * */
  @PostMapping("/myThumb")
  public BR<Page<List<PassageInfoVO>>> myThumbPassage(
      @RequestBody QueryPageRequest queryPageRequest) {
    return R.ok(userService.myThumbPassage(queryPageRequest));
  }

  /*
   * 我的文章，list.size即为我的文章数量
   * */
  @PostMapping("/myPassages")
  public BR<Page<List<PassageInfoVO>>> myPassages(
      @RequestBody QueryPageRequest queryPageRequest) {
    return R.ok(userService.myPassage(queryPageRequest));
  }

  /*
   * 我的消息，用户对我的文章的评论即为我的消息
   * */
  @PostMapping("/myMessage")
  public BR<Page<List<CommentVO>>> myMessage(
      @RequestBody QueryPageRequest queryPageRequest) {
    return R.ok(userService.myMessage(queryPageRequest));
  }


}
