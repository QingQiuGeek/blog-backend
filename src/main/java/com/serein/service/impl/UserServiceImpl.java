package com.serein.service.impl;

import static com.serein.constants.Common.BLOG_CACHE_PREFIX;
import static com.serein.constants.Common.EMAIL_REGEX;
import static com.serein.constants.Common.PASSWORD_REGEX;
import static com.serein.constants.Common.REGISTER_CAPTCHA_TTL;
import static com.serein.constants.Common.USERNAME_REGEX;
import static com.serein.constants.Common.USER_FOLLOW_KEY;
import static com.serein.constants.Common.USER_REGISTER_CAPTCHA_KEY;
import static com.serein.util.RegularUtil.checkMail;
import static com.serein.util.RegularUtil.checkPassword;
import static com.serein.util.RegularUtil.checkUserName;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.Hutool;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.constants.Common;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.exception.BusinessException;
import com.serein.mapper.PassageMapper;
import com.serein.mapper.TagsMapper;
import com.serein.mapper.UserCollectsMapper;
import com.serein.mapper.UserFollowMapper;
import com.serein.mapper.UserMapper;
import com.serein.mapper.UserThumbsMapper;
import com.serein.model.dto.userDTO.AddUserDTO;
import com.serein.model.dto.userDTO.UpdateUserDTO;
import com.serein.model.entity.Comment;
import com.serein.model.entity.Passage;
import com.serein.model.entity.Tags;
import com.serein.model.entity.User;
import com.serein.model.entity.UserCollects;
import com.serein.model.entity.UserFollow;
import com.serein.model.entity.UserThumbs;
import com.serein.model.request.QueryPageRequest;
import com.serein.model.request.UserRequest.AdminUserQueryPageRequest;
import com.serein.model.request.UserRequest.LoginRequest;
import com.serein.model.request.UserRequest.RegisterCodeRequest;
import com.serein.model.request.UserRequest.RegisterRequest;
import com.serein.model.vo.commentVO.CommentVO;
import com.serein.model.vo.passageVO.PassageInfoVO;
import com.serein.model.vo.userVO.AdminUserVO;
import com.serein.model.vo.userVO.LoginUserVO;
import com.serein.model.vo.userVO.UserInfoDataVO;
import com.serein.model.vo.userVO.UserVO;
import com.serein.service.UserService;
import com.serein.util.AliOssUtil;
import com.serein.util.IPUtil;
import com.serein.util.MailUtil;
import com.serein.util.UserContext;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 懒大王Smile
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-09-12 22:19:13
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

  @Value("${custom.originPassword}")
  String originPassword;

  @Value("${spring.mail.username}")
  private String fromEmail;

  @Resource
  private StringRedisTemplate stringRedisTemplate;

  @Resource
  private UserMapper userMapper;

  @Resource
  private UserCollectsMapper userCollectsMapper;

  @Resource
  private UserThumbsMapper userThumbsMapper;

  @Resource
  private PassageServiceImpl passageService;

  @Resource
  private UserFollowMapper userFollowMapper;

  @Resource
  private JavaMailSenderImpl mailSender;

  private MailUtil mailUtil;

  @Resource
  private PassageMapper passageMapper;

  @Resource
  private CommentServiceImpl commentServiceImpl;

  @Resource
  private TagsMapper tagsMapper;

//  @Resource
//  @Qualifier("taskExecutor")
//  private ThreadPoolTaskExecutor taskExecutor;

  /**
   * 关注或取关
   *
   * @param userId
   * @return
   * @Description: 用户的关注信息存在redis中，登录用户的Id为key，被关注的用户Id为value
   */
  @Transactional
  @Override
  public Boolean follow(Long userId) {
    String key = Common.USER_FOLLOW_KEY + userId;
    // 如果返回值为 null，表示用户未关注目标用户；如果返回一个非 null 的分数，表示已经关注。
    Boolean member = stringRedisTemplate.opsForSet().isMember(key, userId.toString());
    if (Boolean.FALSE.equals(member)) {
      //如果用户未关注目标用户，执行关注操作:
      UserFollow userFollow = UserFollow.builder().userId(userId).toUserId(userId).build();
      //先更新数据库 user-follow表
      int insert = userFollowMapper.insert(userFollow);
      if (insert == 1) {
        //再更新redis
        Long add = stringRedisTemplate.opsForSet()
            .add(key, userId.toString());
        if (add != 1L) {
          throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.REDIS_UPDATE_ERROR);
        }
      } else {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
      }
    } else {
      //如果用户已经关注目标用户，执行取消关注操作
      LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(UserFollow::getUserId, userId).eq(UserFollow::getToUserId, userId);
      //delete是被删除的行数，正常情况下是1，因为关注和被关注的关系只有一个存在数据库，不会重复关注
      int delete = userFollowMapper.delete(queryWrapper);
      if (delete == 1) {
        Long remove = stringRedisTemplate.opsForSet().remove(key, userId.toString());
        if (remove != 1) {
          throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.REDIS_UPDATE_ERROR);
        }
      } else {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
      }
    }
    return true;
  }

  /**
   * 我关注的用户列表
   *
   * @return
   * @Description:
   */
  @Override
  public Page<List<UserVO>> myFollow(QueryPageRequest queryPageRequest) {
    Long userId = UserContext.getUser();
    if (userId == null) {
      Page<List<UserVO>> objectPage = new Page<>();
      objectPage.setTotal(0);
      objectPage.setRecords(Collections.emptyList());
      //未登录直接返回空列表
      return objectPage;
      //未登录直接返回空列表
    }
    int currentPage = queryPageRequest.getCurrentPage();
    int pageSize = queryPageRequest.getPageSize();
    Page<UserFollow> userFollowPage = userFollowMapper.selectPage(new Page<UserFollow>(currentPage,pageSize), new LambdaQueryWrapper<UserFollow>().eq(UserFollow::getUserId, userId));
    List<UserFollow> records = userFollowPage.getRecords();
    if (records.isEmpty()) {
      Page<List<UserVO>> objectPage = new Page<>();
      objectPage.setTotal(0);
      objectPage.setRecords(Collections.emptyList());
      //未登录直接返回空列表
      return objectPage;
    }
    List<Long> idList = records.stream().map(UserFollow::getToUserId).collect(Collectors.toList());
    List<User> userList = this.listByIds(idList);
    //把user转化成userVo
    List<UserVO> userVOListByUserList = getUserVOListByUserList(userList);
    //我关注的，全部设置成已关注
    userVOListByUserList.forEach(userVO -> userVO.setIsFollow(true));
    Page<List<UserVO>> listPage = new Page<>(currentPage, pageSize);
    listPage.setRecords(Collections.singletonList(userVOListByUserList));
    listPage.setTotal(userFollowPage.getTotal());
    return listPage;
  }


  //判断我是否关注了这些用户
  private List<UserVO> isFollow(Long userId, List<UserVO> userVOList) {
    List<UserVO> userVOS = new ArrayList<>();
    String key = Common.USER_FOLLOW_KEY + userId.toString();
    for (UserVO userVO : userVOList) {
      Boolean follow = stringRedisTemplate.opsForSet().isMember(key, userVO.getUserId().toString());
      userVO.setIsFollow(follow);
      userVOS.add(userVO);
    }
    return userVOS;
  }

  /**
   * 我的粉丝列表，要判断是否关注了某些粉丝
   *
   * @return
   * @Description: redis存储了我关注了哪些用户，适合查询我的关注，而不是我的粉丝，这里查mysql
   */
  @Override
  public Page<List<UserVO>> myFollowers(QueryPageRequest queryPageRequest) {
    Long userId = UserContext.getUser();
    if (userId == null) {
      Page<List<UserVO>> objectPage = new Page<>();
      objectPage.setTotal(0);
      objectPage.setRecords(Collections.emptyList());
      //未登录直接返回空列表
      return objectPage;
    }
    
    int pageSize = queryPageRequest.getPageSize();
    int currentPage = queryPageRequest.getCurrentPage();
    Page<UserFollow> userFollowPage = userFollowMapper.selectPage(new Page<>(currentPage, pageSize),
        new LambdaQueryWrapper<UserFollow>().eq(UserFollow::getToUserId, userId)
            .orderByDesc(UserFollow::getFollowTime));
    List<UserFollow> records = userFollowPage.getRecords();
    if (CollUtil.isEmpty(records)) {
      Page<List<UserVO>> objectPage = new Page<>();
      objectPage.setTotal(0);
      objectPage.setRecords(Collections.emptyList());
      //未登录直接返回空列表
      return objectPage;
    }
    long total = userFollowPage.getTotal();
    ArrayList<Long> idList = new ArrayList<>();
    //拿到我的粉丝的id列表
    records.forEach(myFollower -> idList.add(myFollower.getUserId()));
    //根据我的粉丝的idList查询出来粉丝的userVO
    List<User> userList = this.listByIds(idList);
    List<UserVO> userVOListByUserList = getUserVOListByUserList(userList);
    //判断我是否关注了粉丝
    List<UserVO> follow = isFollow(userId, userVOListByUserList);
    Page<List<UserVO>> listPage = new Page<>(currentPage, pageSize);
    listPage.setRecords(Collections.singletonList(follow));
    listPage.setTotal(total);
    return listPage;
  }

  /**
   * @param uid
   * @return 获取用户的信息，展示在文章详情页
   */
  @Cacheable(cacheNames = BLOG_CACHE_PREFIX+"userInfo",key = "#p0")
  @Override
  public UserVO getUserInfo(Long uid) {
    User byId = this.getById(uid);
    if (byId != null) {
      UserVO userVO = new UserVO();
      BeanUtil.copyProperties(byId, userVO);
      String ipRegion = IPUtil.getIpRegion(byId.getIpAddress());
      userVO.setIpAddress(ipRegion);
      String interestTag = byId.getInterestTag();
      if (StringUtils.isNotBlank(interestTag)) {
        List<Long> tagIdList = JSONUtil.toList(JSONUtil.parseArray(interestTag), Long.class);
        List<Tags> tags = tagsMapper.selectByIds(tagIdList);
        List<String> tagNameList = tags.stream().map(Tags::getTagName).collect(Collectors.toList());
        userVO.setInterestTag(tagNameList);
      }
      //查询该用户粉丝数量
      int followerNum = userFollowMapper.getFollowerNum(uid);
      userVO.setFollowerNum(followerNum);
      Long userId = UserContext.getUser();
      if (userId == null) {
        return userVO;
      }
      
      List<UserVO> userVOS = new ArrayList<>();
      userVOS.add(userVO);
      isFollow(userId, userVOS);
      return userVO;
    }
    return null;
  }


  /**
   * 获取个人主页展示的粉丝数量、文章收藏量、作品数量、关注数量、点赞数量
   *
   * @return
   */
  @Override
  public UserInfoDataVO getUserInfoData() {
    UserInfoDataVO userInfoDataVO = new UserInfoDataVO();
    //未登录返回默认数据0
    Long userId = UserContext.getUser();
    if (userId == null) {
      return userInfoDataVO;
    }
    
    int followerNum = userFollowMapper.getFollowerNum(userId);
    int collectNum = passageMapper.getTotalCollectNumById(userId);
    int passageNum = passageMapper.getPassageNumById(userId);
    String key = USER_FOLLOW_KEY + userId;
    Long followNum = stringRedisTemplate.opsForSet().size(key);
    int thumbNum = passageMapper.getTotalThumbNum(userId);
    userInfoDataVO.setCollectNum(collectNum);
    userInfoDataVO.setFollowNum(followNum==null?0:followNum.intValue());
    userInfoDataVO.setPassageNum(passageNum);
    userInfoDataVO.setThumbNum(thumbNum);
    userInfoDataVO.setFollowerNum(followerNum);
    return userInfoDataVO;
  }

  @Override
  public Page<List<CommentVO>> myMessage(QueryPageRequest queryPageRequest) {
    Page<List<CommentVO>> listPage = new Page<List<CommentVO>>();
    Long userId = UserContext.getUser();

    if (userId == null) {
      //未登录直接返回空列表
      listPage.setTotal(0);
      listPage.setRecords(Collections.emptyList());
      return listPage;
    }
    
    int pageSize = queryPageRequest.getPageSize();
    int currentPage = queryPageRequest.getCurrentPage();
    Page<Comment> commentPage = new Page<>(currentPage, pageSize);
    Page<Comment> page = commentServiceImpl.page(commentPage,
        new LambdaQueryWrapper<Comment>().eq(Comment::getAuthorId, userId)
            .orderByDesc(Comment::getCommentTime));
    List<Comment> records = page.getRecords();
    if (records.isEmpty()) {
      listPage.setTotal(0);
      listPage.setRecords(Collections.emptyList());
      return listPage;
    }
    List<CommentVO> commentVOS = commentServiceImpl.getCommentVOList(records);
    //设置评论的用户头像、ip地址、用户名
    commentServiceImpl.getCommentUserInfo(commentVOS);
    //全部设置可删除
    commentVOS.forEach((commentVO -> {
      commentVO.setCanDelete(true);
    }));
    listPage.setTotal(page.getTotal());
    listPage.setRecords(Collections.singletonList(commentVOS));
    return listPage;
  }

  @Override
  public String uploadAvatar(MultipartFile file) {
    Long userId = UserContext.getUser();

    if (userId == null) {
      throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, ErrorInfo.NOT_LOGIN_ERROR);
    }
    String avatarUrl = AliOssUtil.uploadImageOSS(file);
    
    boolean b = userMapper.updateAvatar(userId, avatarUrl);
    if (!b) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
    }
    log.info("update avatarUrl：" + avatarUrl);
    return avatarUrl;
  }

  @Override
  public Page<List<PassageInfoVO>> myCollectPassage(QueryPageRequest queryPageRequest) {
    Long userId = UserContext.getUser();

    if (userId == null) {
      Page<List<PassageInfoVO>> objectPage = new Page<>();
      objectPage.setTotal(0);
      objectPage.setRecords(Collections.emptyList());
      //未登录直接返回空列表
      return objectPage;
    }
    
    int pageSize = queryPageRequest.getPageSize();
    int currentPage = queryPageRequest.getCurrentPage();
    Page<UserCollects> userCollectsPage = userCollectsMapper.selectPage(
        new Page<>(currentPage, pageSize),
        new LambdaQueryWrapper<UserCollects>().eq(UserCollects::getUserId, userId)
            .orderByDesc(UserCollects::getCollectTime));
    List<UserCollects> records = userCollectsPage.getRecords();
    if (CollUtil.isEmpty(records)) {
      Page<List<PassageInfoVO>> objectPage = new Page<>();
      objectPage.setTotal(0);
      objectPage.setRecords(Collections.emptyList());
      //未登录直接返回空列表
      return objectPage;
    }
    ArrayList<Long> passageIdList = new ArrayList<>();
    records.forEach(userCollects1 -> passageIdList.add(userCollects1.getPassageId()));
    long total = userCollectsPage.getTotal();
    List<Passage> passageList = passageService.listByIds(passageIdList);
    List<PassageInfoVO> passageInfoVOList = passageService.getPassageInfoVOList(passageList);
    Page<List<PassageInfoVO>> listPage = new Page<List<PassageInfoVO>>(currentPage, pageSize);
    listPage.setTotal(total);
    listPage.setRecords(Collections.singletonList(passageInfoVOList));
    return listPage;
  }

  @Override
  public Page<List<PassageInfoVO>> myThumbPassage(QueryPageRequest queryPageRequest) {
    Long userId = UserContext.getUser();

    if (userId == null) {
      //未登录返回空列表
      Page<List<PassageInfoVO>> listPage = new Page<>();
      listPage.setTotal(0);
      listPage.setRecords(Collections.emptyList());
      return listPage;
    }
    
    int currentPage = queryPageRequest.getCurrentPage();
    int pageSize = queryPageRequest.getPageSize();

    Page<UserThumbs> thumbsPage = userThumbsMapper.selectPage(new Page<>(currentPage, pageSize),
        new LambdaQueryWrapper<UserThumbs>().eq(UserThumbs::getUserId, userId)
            .orderByDesc(UserThumbs::getThumbTime));

    List<UserThumbs> records = thumbsPage.getRecords();
    if (CollUtil.isEmpty(records)) {
      Page<List<PassageInfoVO>> listPage = new Page<>();
      listPage.setTotal(0);
      listPage.setRecords(Collections.emptyList());
      return listPage;
    }
    long total = thumbsPage.getTotal();
    ArrayList<Long> passageIdList = new ArrayList<>();
    records.forEach(userThumbs1 -> passageIdList.add(userThumbs1.getPassageId()));
    List<Passage> passageList = passageService.listByIds(passageIdList);
    List<PassageInfoVO> passageInfoVOList = passageService.getPassageInfoVOList(passageList);
    Page<List<PassageInfoVO>> passageInfoVOPage = new Page<>(currentPage, pageSize);
    Page<List<PassageInfoVO>> listPage = passageInfoVOPage.setRecords(
        Collections.singletonList(passageInfoVOList));
    listPage.setTotal(total);
    return listPage;
  }

  @Override
  public Page<List<PassageInfoVO>> myPassage(QueryPageRequest queryPageRequest) {
    Long userId = UserContext.getUser();

    if (userId == null) {
      Page<List<PassageInfoVO>> objectPage = new Page<>();
      objectPage.setTotal(0);
      objectPage.setRecords(Collections.emptyList());
      //未登录直接返回空列表
      return objectPage;
    }
    
    int currentPage = queryPageRequest.getCurrentPage();
    int pageSize = queryPageRequest.getPageSize();
    Page<Passage> passagePage = passageMapper.selectPage(new Page<>(currentPage, pageSize),
        new LambdaQueryWrapper<Passage>().eq(Passage::getAuthorId, userId)
            .select(Passage::getPassageId, Passage::getTitle, Passage::getContent,
                Passage::getStatus,
                Passage::getIsPrivate,
                Passage::getViewNum, Passage::getAccessTime, Passage::getThumbnail,
                Passage::getSummary, Passage::getAuthorId)
            .orderByDesc(Passage::getCreateTime));
    List<Passage> records = passagePage.getRecords();
    if (CollUtil.isEmpty(records)) {
      Page<List<PassageInfoVO>> objectPage = new Page<>();
      objectPage.setTotal(0);
      objectPage.setRecords(Collections.emptyList());
      //未登录直接返回空列表
      return objectPage;
    }
    long total = passagePage.getTotal();
    List<PassageInfoVO> passageInfoVOList = passageService.getPassageInfoVOList(records);
    Page<List<PassageInfoVO>> listPage = new Page<>(currentPage, pageSize);
    listPage.setRecords(Collections.singletonList(passageInfoVOList));
    listPage.setTotal(total);
    return listPage;
  }


  /**
   * @param loginRequest
   * @return
   */
  @Override
  public LoginUserVO login(LoginRequest loginRequest) {
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (requestAttributes == null) {
      throw new BusinessException(ErrorCode.UNEXPECT_ERROR, ErrorInfo.SYS_ERROR);
    }

    //1.判断邮箱和密码是否为空,邮箱格式校验，密码长度校验
    String loginUserMail = loginRequest.getMail();
    String loginPassword = loginRequest.getPassword();
    if (StringUtils.isAnyBlank(loginUserMail, loginPassword) || checkMail(loginUserMail)
        || checkPassword(loginPassword)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.PARAMS_ERROR);
    }

    //2.根据邮箱从数据库查询用户
    User queryUser = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getMail, encrypt(loginUserMail)));
    if (queryUser == null) {
      throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, ErrorInfo.NO_REGISTER);
    }
    if (queryUser.getStatus() == 0) {
      throw new BusinessException(ErrorCode.NO_AUTH_ERROR, ErrorInfo.BAN_ACCOUNT);
    }

    //3.核对密码
    {
      if (!encrypt(loginPassword).equals(queryUser.getPassword())) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.MAIL_OR_PASSWORD_ERROR);
      }
    }

    HttpServletRequest request = requestAttributes.getRequest();
    String ipAddr = IPUtil.getIpAddr(request);
    log.info("用户登录，ip地址：{}", ipAddr);
    if (!queryUser.getIpAddress().equals(ipAddr)) {
      //如果用户id地址变化，那么更新数据库
      userMapper.updateIpAddress(ipAddr, queryUser.getUserId());
    }
    return loginSuccess(queryUser.getRole(), queryUser.getUserId());
  }

  /**
   * 传入参数，使用参数本身作为盐值 进行加密
   *
   * @param str
   * @return
   */
  public String encrypt(String str) {
    return DigestUtils.md5DigestAsHex((str + str).getBytes());
  }

  /**
   * @param role
   */
  private LoginUserVO loginSuccess(String role, Long userId) {
    LoginUserVO loginUserVO = new LoginUserVO();
//    String token = UUID.randomUUID(true).toString();
//    loginUserVO.setToken(token);
    loginUserVO.setUserId(userId);
    loginUserVO.setRole(role);
    loginUserVO.setAvatarUrl(userMapper.getUserAvatar(userId));
//    Map<String, String> stringMap = new HashMap<>();
//    stringMap.put("userId", userId.toString());
//    stringMap.put("role", role);
//    String tokenKey = Common.LOGIN_TOKEN_KEY + token;
//    stringRedisTemplate.opsForHash().putAll(tokenKey, stringMap);
//    stringRedisTemplate.expire(tokenKey, Common.LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
    //以LOGIN_TOKEN_KEY+userid为key，userId+role序列化map存到redis
    UserContext.saveUser(userId);
    StpUtil.login(userId);
    return loginUserVO;
  }

  @Async("taskExecutor")
  public Boolean sendCodeForRegister(String email) {
    log.info("尝试发送邮箱验证码给用户：" + email + "进行注册操作");
    log.info("开始发送邮件..." + "获取的到邮件发送对象为:" + mailSender);
    mailUtil = new MailUtil(mailSender, fromEmail);
    String code = mailUtil.sendCode(email);
    //验证码存入redis，有效期1min,用注册的邮箱区分验证码
    stringRedisTemplate.opsForValue()
        .set(USER_REGISTER_CAPTCHA_KEY + encrypt(email), code, REGISTER_CAPTCHA_TTL, TimeUnit.MINUTES);
    log.info("发送邮箱验证码给用户：" + email + "成功 : " + code);
    return StringUtils.isNotBlank(code);
  }

  /**
   * @param registerCodeRequest
   */
  @Override
  public Boolean sendRegisterCode(RegisterCodeRequest registerCodeRequest) {
    String mail = registerCodeRequest.getMail();
    //检查该邮箱是否已注册
    checkMailIsRegistered(mail);
    //发送验证码
    return sendCodeForRegister(mail);
  }

  /**
   * @param registerRequest
   * @return
   */
  @Override
  public LoginUserVO register(RegisterRequest registerRequest) {
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (requestAttributes == null) {
      throw new BusinessException(ErrorCode.UNEXPECT_ERROR, ErrorInfo.SYS_ERROR);
    }
    String mail = registerRequest.getMail();
    String password = registerRequest.getPassword();
    String rePassword = registerRequest.getRePassword();
    String userName = registerRequest.getUserName();
    String registerCode = registerRequest.getCode();

    //检查注册参数是否为空
    if (StringUtils.isAnyBlank(mail, password, rePassword, userName, registerCode)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.PARAMS_ERROR);
    }

    // 检查参数是否合法
    if (!password.equals(rePassword) || checkMail(mail) || checkUserName(userName) || checkPassword(
        password)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.PARAMS_ERROR);
    }

    //检查邮箱是否被注册
    checkMailIsRegistered(mail);

    //从redis获取验证码
    String rightCode = stringRedisTemplate.opsForValue().get(USER_REGISTER_CAPTCHA_KEY + encrypt(mail));

    //检查验证码是否存在
    if (StringUtils.isBlank(rightCode)) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.CAPTCHA_ERROR);
    }

    //核验验证码是否正确
    if (!rightCode.equals(registerCode)) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.CAPTCHA_ERROR);
    }

    //4.密码盐值加密，写入数据库，注册成功
    HttpServletRequest request = requestAttributes.getRequest();
    String ipAddr = IPUtil.getIpAddr(request);
    log.info("用户注册，ip地址：{}", ipAddr);
    String ipRegion = IPUtil.getIpRegion(ipAddr);
    log.info("用户注册，ip归属地：{}", ipRegion);
    User user = User.builder().userName(userName).password(encrypt(password)).mail(encrypt(mail)).ipAddress(ipAddr)
        .build();
    int insert = userMapper.insert(user);
    if (insert <= 0) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.REGISTER_ERROR);
    }
    return loginSuccess(userMapper.getUserRole(user.getUserId()), user.getUserId());
  }

  private void checkMailIsRegistered(String mail) {
    //发送验证码时已检查该邮箱是否注册，这里再检查一次
    LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
    userLambdaUpdateWrapper.eq(User::getMail, encrypt(mail));
    if (userMapper.exists(userLambdaUpdateWrapper)) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.MAIL_EXISTED_ERROR);
    }
  }


  /**
   * @return
   */
  @Override
  public Boolean logout(HttpServletRequest httpServletRequest) {
    try {
      StpUtil.logout();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
//    //获取请求头中的token，这是用户登录时生成的uuid
//    String token = httpServletRequest.getHeader(AUTHORIZATION);
//    if (StringUtils.isBlank(token)) {
//      throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效的token");
//    }
//    String tokenKey = Common.LOGIN_TOKEN_KEY + token;
//    return stringRedisTemplate.delete(tokenKey);
    return true;
  }


  @Override
  public LoginUserVO getLoginUser() {
    Long userId = UserContext.getUser();
    if (userId == null) {
      throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "获取当前登录用户失败");
    }
    //获取数据库最新的数据，防止用户更新完个人信息后拿到的还是老数据
    User user = userMapper.selectById(userId);
    LoginUserVO loginUserVO = new LoginUserVO();
    BeanUtils.copyProperties(user, loginUserVO);
    String ipAddress = user.getIpAddress();
    String ipRegion = IPUtil.getIpRegion(ipAddress);
    loginUserVO.setIpAddress(ipRegion);
    String interestTag = user.getInterestTag();
    if (StringUtils.isNotBlank(interestTag)) {
      List<Long> tagIdlist = JSONUtil.toList(JSONUtil.parseArray(interestTag), Long.class);
      if (CollUtil.isNotEmpty(tagIdlist)) {
        List<Tags> tags = tagsMapper.selectByIds(tagIdlist);
        List<String> tagNameList = tags.stream().map(Tags::getTagName).collect(Collectors.toList());
        loginUserVO.setInterestTag(tagNameList);
      }
    }
    return loginUserVO;
  }


  @Override
  public Page<List<AdminUserVO>> getUserList(AdminUserQueryPageRequest adminUserQueryPageRequest) {
    int currentPage = adminUserQueryPageRequest.getCurrentPage();
    int pageSize = adminUserQueryPageRequest.getPageSize();
    String userName = adminUserQueryPageRequest.getUserName();
    Date endTime = adminUserQueryPageRequest.getEndTime();
    Date startTime = adminUserQueryPageRequest.getStartTime();
    Long userId = adminUserQueryPageRequest.getUserId();
    Page<User> userPage = new Page<>(currentPage, pageSize);
    Page<User> userDesc = page(userPage,
        new LambdaQueryWrapper<User>().orderByDesc(User::getCreateTime)
            .gt(startTime != null, User::getCreateTime, startTime)
            .lt(endTime != null, User::getCreateTime, endTime)
            .eq(userId != null, User::getUserId, userId)
            .eq(StringUtils.isNotBlank(userName), User::getUserName, userName)
            .select(User::getUserId, User::getStatus, User::getUserName, User::getInterestTag,
                User::getMail, User::getRole, User::getCreateTime));
    List<User> records = userDesc.getRecords();
    long total = userDesc.getTotal();
    Page<List<AdminUserVO>> listPage = new Page<>(currentPage, pageSize);
    if (records.isEmpty()) {
      //包装成单一的list
      listPage.setRecords(Collections.singletonList(Collections.emptyList()));
      //总数据数量
      listPage.setTotal(0);
      return listPage;
//      throw new BusinessException(ErrorCode.PARAMS_ERROR, "获取用户列表失败");
    }
    List<AdminUserVO> adminUserListByUserList = getAdminUserVOListByUserList(records);
    //包装成单一的list
    listPage.setRecords(Collections.singletonList(adminUserListByUserList));
    //总数据数量
    listPage.setTotal(total);
    return listPage;
  }

  @Override
  public List<UserVO> getUserListByName(String userName) {
    LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(User::getUserName, userName).eq(User::getStatus, 1);
    List<User> userList = this.list(queryWrapper);
    if (userList.isEmpty()) {
      return Collections.emptyList();
    }
    return getUserVOListByUserList(userList);
  }

  public List<AdminUserVO> getAdminUserVOListByUserList(List<User> userList) {
    return userList.stream().map(user -> {
      AdminUserVO adminUserVO = new AdminUserVO();
      BeanUtils.copyProperties(user, adminUserVO);
      if (StringUtils.isNotBlank(user.getInterestTag())) {
        List<Long> tagIdList = JSONUtil.toList(JSONUtil.parseArray(user.getInterestTag()),
            Long.class);
        List<Tags> tagList;
        if(CollUtil.isNotEmpty(tagIdList)) {
          tagList = tagsMapper.selectByIds(tagIdList);
          List<String> tagNameList = tagList.stream().map(Tags::getTagName)
              .collect(Collectors.toList());
          adminUserVO.setInterestTag(tagNameList);
        }
      }
      return adminUserVO;
    }).collect(Collectors.toList());
  }

  /**
   * 把userList转成userVOList
   *
   * @param userList
   * @return
   */
  public List<UserVO> getUserVOListByUserList(List<User> userList) {
    return userList.stream().map(user -> {
      UserVO userVO = new UserVO();
      BeanUtils.copyProperties(user, userVO);
      if (StringUtils.isNotBlank(user.getInterestTag())) {
        List<Long> tagIdList = JSONUtil.toList(JSONUtil.parseArray(user.getInterestTag()),
            Long.class);
        List<Tags> tags = tagsMapper.selectByIds(tagIdList);
        List<String> tagNameList = tags.stream().map(Tags::getTagName).collect(Collectors.toList());
        userVO.setInterestTag(tagNameList);
      }
      return userVO;
    }).collect(Collectors.toList());
  }

  @Override
  public List<AdminUserVO> getByIdList(List<Long> idList) {

    List<User> userList = this.listByIds(idList);
    if (userList.isEmpty()) {
      throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, ErrorInfo.NO_DB_DATA);
    }
    return getAdminUserVOListByUserList(userList);
  }

  @Override
  public Boolean banUser(Long userId) {
    User byId = getById(userId);
    Integer status = byId.getStatus();
    LambdaUpdateWrapper<User> userUpdateWrapper = new LambdaUpdateWrapper<>();
    if (status == 0) {
      userUpdateWrapper.eq(User::getUserId, userId).set(User::getStatus, 1);
    } else {
      userUpdateWrapper.eq(User::getUserId, userId).set(User::getStatus, 0);
    }
    boolean b = this.update(userUpdateWrapper);
    if (b) {
      return true;
    }
    throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
  }

  @Override
  public Boolean updateUser(UpdateUserDTO updateUserDTO) {
    User user = new User();
    Long userId = UserContext.getUser();
    user.setUserId(userId);
    BeanUtil.copyProperties(updateUserDTO, user);
//    updateUserDTO.getInterestTag()
    if("[null]".equals(user.getInterestTag())||"[ ]".equals(user.getInterestTag())){
      user.setInterestTag("");
    }
    boolean b = this.updateById(user);
    if (!b) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
    }
    return true;
  }

  /**
   * 管理添加用户时可以设置密码
   *
   * @param addUserDTO
   * @return
   */
  @Override
  public Long addUser(AddUserDTO addUserDTO) {
    User addUser = new User();
    BeanUtil.copyProperties(addUserDTO, addUser);
    addUser.setPassword(encrypt(originPassword));
    boolean save = this.save(addUser);
    if (save) {
      return addUser.getUserId();
    }
    throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.ADD_ERROR);
  }


}




