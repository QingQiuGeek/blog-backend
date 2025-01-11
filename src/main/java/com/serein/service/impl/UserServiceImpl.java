package com.serein.service.impl;

import static com.serein.constants.Common.EMAIL_REGEX;
import static com.serein.constants.Common.PASSWORD_REGEX;
import static com.serein.constants.Common.REGISTER_CODE_TTL;
import static com.serein.constants.Common.USERNAME_REGEX;
import static com.serein.constants.Common.USER_FOLLOW_KEY;
import static com.serein.constants.Common.USER_REGISTER_CODE_KEY;

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
import com.serein.constants.UserRole;
import com.serein.exception.BusinessException;
import com.serein.mapper.CommentMapper;
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
import com.serein.util.FileUtil;
import com.serein.util.IPUtil;
import com.serein.util.MailUtil;
import com.serein.util.UserHolder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

  //盐值.从yml文件获取
  @Value("${custom.salt}")
  String SALT;

  @Value("${custom.originPassword}")
  String originPassword;

  @Value("${spring.mail.username}")
  private String fromEmail;

  @Resource
  private  StringRedisTemplate stringRedisTemplate;

  @Resource
  private  UserMapper userMapper;

  @Resource
  private UserCollectsMapper userCollectsMapper;

  @Resource
  private UserThumbsMapper userThumbsMapper;

  @Resource
  private PassageServiceImpl passageService;

  @Resource
  private  UserFollowMapper userFollowMapper;

  @Resource
  private JavaMailSenderImpl mailSender;

  @Resource
  private MailUtil mailUtil;

  @Resource
  private PassageMapper passageMapper;

  @Resource
  private CommentServiceImpl commentServiceImpl;

  @Resource
  private CommentMapper commentMapper;

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
    LoginUserVO loginUserVO = UserHolder.getUser();
    if (loginUserVO == null) {
      throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, ErrorInfo.NOT_LOGIN_ERROR);
    }
    Long loginUserId = loginUserVO.getUserId();
    String key = Common.USER_FOLLOW_KEY + loginUserId;
    //使用 stringRedisTemplate.opsForZSet().score(key, loginUserId.toString()) 查询当前登录用户是否已经关注了目标用户。
    // 如果返回值为 null，表示用户未关注目标用户；如果返回一个非 null 的分数，表示已经关注。
    Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
    if (score == null) {
      //如果用户未关注目标用户，执行关注操作:
      UserFollow userFollow = UserFollow.builder().userId(loginUserId).toUserId(userId).build();
      //先更新数据库 user-follow表
      int insert = userFollowMapper.insert(userFollow);
      if (insert == 1) {
        //再更新redis
        Boolean add = stringRedisTemplate.opsForZSet()
            .add(key, userId.toString(), System.currentTimeMillis());
        if (Boolean.FALSE.equals(add)) {
          throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.REDIS_UPDATE_ERROR);
        }
      } else {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
      }
    } else {
      //如果用户已经关注目标用户，执行取消关注操作
      LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(UserFollow::getUserId, loginUserId).eq(UserFollow::getToUserId, userId);
      //delete是被删除的行数，正常情况下是1，因为关注和被关注的关系只有一个存在数据库，不会重复关注
      int delete = userFollowMapper.delete(queryWrapper);
      if (delete == 1) {
        Long remove = stringRedisTemplate.opsForZSet().remove(key, userId.toString());
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
   * @Description: 从redis查
   */
  @Override
  public Page<List<UserVO>> myFollow(QueryPageRequest queryPageRequest) {
    LoginUserVO loginUserVO = UserHolder.getUser();
    if (loginUserVO == null) {
      Page<List<UserVO>> objectPage = new Page<>();
      objectPage.setTotal(0);
      objectPage.setRecords(Collections.emptyList());
      //未登录直接返回空列表
      return objectPage;
      //未登录直接返回空列表
    }
    Long loginUserId = loginUserVO.getUserId();
    String key = USER_FOLLOW_KEY + loginUserId;
    //从redis查
    int currentPage = queryPageRequest.getCurrentPage();
    int pageSize = queryPageRequest.getPageSize();
    Set<String> stringIdSet = stringRedisTemplate.opsForZSet()
        .range(key, (long) (currentPage - 1) * pageSize, (long) currentPage * pageSize - 1);
    if (CollUtil.isEmpty(stringIdSet)) {
      Page<List<UserVO>> objectPage = new Page<>();
      objectPage.setTotal(0);
      objectPage.setRecords(Collections.emptyList());
      //未登录直接返回空列表
      return objectPage;
    }
    ArrayList<Long> idList = new ArrayList<>();
    //遍历stringIdSet把每一个string类型的userid转换成long
    stringIdSet.forEach(idString -> idList.add(Long.valueOf(idString)));
    List<User> userList = this.listByIds(idList);
    //把user转化成uservo
    List<UserVO> userVOListByUserList = getUserVOListByUserList(userList);
    //我关注的，全部设置成已关注
    userVOListByUserList.forEach(userVO -> userVO.setIsFollow(true));
    int size = stringIdSet.size();
    Page<List<UserVO>> listPage = new Page<>(currentPage, pageSize);
    listPage.setRecords(Collections.singletonList(userVOListByUserList));
    listPage.setTotal((long) size);
    return listPage;
  }


  //判断我是否关注了这些用户
  private List<UserVO> isFollow(Long loginUserId, List<UserVO> userVOList) {
    List<UserVO> userVOS = new ArrayList<>();
    String key = Common.USER_FOLLOW_KEY + loginUserId.toString();
    for (UserVO userVO : userVOList) {
      Double score = stringRedisTemplate.opsForZSet().score(key, userVO.getUserId().toString());
      userVO.setIsFollow(score != null);
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
    LoginUserVO loginUserVO = UserHolder.getUser();
    if (loginUserVO == null) {
      Page<List<UserVO>> objectPage = new Page<>();
      objectPage.setTotal(0);
      objectPage.setRecords(Collections.emptyList());
      //未登录直接返回空列表
      return objectPage;
    }
    Long loginUserId = loginUserVO.getUserId();
    int pageSize = queryPageRequest.getPageSize();
    int currentPage = queryPageRequest.getCurrentPage();
    Page<UserFollow> userFollowPage = userFollowMapper.selectPage(new Page<>(currentPage, pageSize),
        new LambdaQueryWrapper<UserFollow>().eq(UserFollow::getToUserId, loginUserId)
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
    //根据我的粉丝的idlist查询出来粉丝的uservo
    List<User> userList = this.listByIds(idList);
    List<UserVO> userVOListByUserList = getUserVOListByUserList(userList);
    //判断我是否关注了粉丝
    List<UserVO> follow = isFollow(loginUserId, userVOListByUserList);
    Page<List<UserVO>> listPage = new Page<>(currentPage, pageSize);
    listPage.setRecords(Collections.singletonList(follow));
    listPage.setTotal(total);
    return listPage;
  }

  /**
   * @param uid
   * @return 获取其他用户的信息，展示在其他用户的主页或者文章详情页
   */
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
        List<Tags> tags = tagsMapper.selectBatchIds(tagIdList);
        List<String> tagNameList = tags.stream().map(Tags::getTagName).collect(Collectors.toList());
        userVO.setInterestTag(tagNameList);
      }
      //查询该用户粉丝数量
      int followerNum = userFollowMapper.getFollowerNum(uid);
      userVO.setFollowerNum(followerNum);
      LoginUserVO loginUserVO = UserHolder.getUser();
      if (loginUserVO == null) {
        return userVO;
      }

      Long loginUserId = loginUserVO.getUserId();
      List<UserVO> userVOS = new ArrayList<>();
      userVOS.add(userVO);
      isFollow(loginUserId, userVOS);
      return userVO;
    }
    return null;
  }

  @Override
  public Boolean setAdmin(Long userId) {
    User byId = getById(userId);
    String role = byId.getRole();
    LambdaUpdateWrapper<User> userUpdateWrapper = new LambdaUpdateWrapper<>();
    if (role.equals(UserRole.ADMIN_ROLE)) {
      userUpdateWrapper.eq(User::getUserId, userId).set(User::getRole, UserRole.DEFAULT_ROLE);
    } else {
      userUpdateWrapper.eq(User::getUserId, userId).set(User::getRole, UserRole.ADMIN_ROLE);
    }
    boolean b = this.update(userUpdateWrapper);
    if (b) {
      return true;
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
    LoginUserVO loginUserVO = UserHolder.getUser();
    //未登录返回默认数据0
    if (loginUserVO == null) {
      return userInfoDataVO;
    }
    Long userId = loginUserVO.getUserId();
    int followerNum = userFollowMapper.getFollowerNum(userId);
    int collectNum = passageMapper.getCollectNumById(userId);
    int passageNum = passageMapper.getPassageNumById(userId);
    String key = USER_FOLLOW_KEY + userId;
    Long followNum = stringRedisTemplate.opsForZSet().size(key);
    if (followNum == null) {
      followNum = 0L;
    }
    int thumbNum = passageMapper.getThumbNum(userId);
    userInfoDataVO.setCollectNum(collectNum);
    userInfoDataVO.setFollowNum(followNum.intValue());
    userInfoDataVO.setPassageNum(passageNum);
    userInfoDataVO.setThumbNum(thumbNum);
    userInfoDataVO.setFollowerNum(followerNum);
    return userInfoDataVO;
  }

  @Override
  public Page<List<CommentVO>> myMessage(QueryPageRequest queryPageRequest) {
    LoginUserVO loginUserVO = UserHolder.getUser();
    Page<List<CommentVO>> listPage = new Page<List<CommentVO>>();
    if (loginUserVO == null) {
      //未登录直接返回空列表
      listPage.setTotal(0);
      listPage.setRecords(Collections.emptyList());
      return listPage;
    }
    Long userId = loginUserVO.getUserId();
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
    LoginUserVO loginUserVO = UserHolder.getUser();
    if (loginUserVO == null) {
      throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, ErrorInfo.NOT_LOGIN_ERROR);
    }
    String avatarUrl = FileUtil.uploadImageLocal(file);
    Long userId = loginUserVO.getUserId();
    boolean b = userMapper.updateAvatar(userId, avatarUrl);
    if (!b) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
    }
    log.info("update avatarUrl：" + avatarUrl);
    return avatarUrl;
  }

  @Override
  public Page<List<PassageInfoVO>> myCollectPassage(QueryPageRequest queryPageRequest) {
    LoginUserVO loginUserVO = UserHolder.getUser();
    if (loginUserVO == null) {
      Page<List<PassageInfoVO>> objectPage = new Page<>();
      objectPage.setTotal(0);
      objectPage.setRecords(Collections.emptyList());
      //未登录直接返回空列表
      return objectPage;
    }
    Long userId = loginUserVO.getUserId();
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
    LoginUserVO loginUserVO = UserHolder.getUser();
    if (loginUserVO == null) {
      //未登录返回空列表
      Page<List<PassageInfoVO>> listPage = new Page<>();
      listPage.setTotal(0);
      listPage.setRecords(Collections.emptyList());
      return listPage;
    }
    Long loginUserId = loginUserVO.getUserId();
    int currentPage = queryPageRequest.getCurrentPage();
    int pageSize = queryPageRequest.getPageSize();

    Page<UserThumbs> thumbsPage = userThumbsMapper.selectPage(new Page<>(currentPage, pageSize),
        new LambdaQueryWrapper<UserThumbs>().eq(UserThumbs::getUserId, loginUserId)
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
    LoginUserVO loginUserVO = UserHolder.getUser();
    if (loginUserVO == null) {
      Page<List<PassageInfoVO>> objectPage = new Page<>();
      objectPage.setTotal(0);
      objectPage.setRecords(Collections.emptyList());
      //未登录直接返回空列表
      return objectPage;
    }
    Long loginUserId = loginUserVO.getUserId();
    int currentPage = queryPageRequest.getCurrentPage();
    int pageSize = queryPageRequest.getPageSize();
    Page<Passage> passagePage = passageMapper.selectPage(new Page<>(currentPage, pageSize),
        new LambdaQueryWrapper<Passage>().eq(Passage::getAuthorId, loginUserId)
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
      throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.LOGIN_INFO_ERROR);
    }

    //2.根据邮箱从数据库查询用户
    LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(User::getMail, loginUserMail);
    User queryUser = userMapper.selectOne(queryWrapper);
    if (queryUser == null) {
      throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, ErrorInfo.NO_DB_DATA);
    }
    if (queryUser.getStatus() == 0) {
      throw new BusinessException(ErrorCode.NO_AUTH_ERROR, ErrorInfo.BAN_ACCOUNT);
    }
    //核对密码
    {
      if (!DigestUtils.md5DigestAsHex((SALT + loginPassword).getBytes())
          .equals(queryUser.getPassword())) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.PASSWORD_ERROR);
      }
    }
    HttpServletRequest request = requestAttributes.getRequest();
    String ipAddr = IPUtil.getIpAddr(request);
    log.info("用户登录，ip地址：{}", ipAddr);
    if (!queryUser.getIpAddress().equals(ipAddr)) {
      //如果用户id地址变化，那么更新数据库
      userMapper.updateIpAddress(ipAddr, queryUser.getUserId());
    }
    LoginUserVO loginUserVO = loginSuccess(queryUser.getRole(),queryUser.getUserId());
    log.info("loginUserVO：" + loginUserVO);
    return loginUserVO;
  }

  /**
   * @param role
   */
  private LoginUserVO loginSuccess(String role,Long userId) {
    LoginUserVO loginUserVO = new LoginUserVO();
    String token = UUID.randomUUID(true).toString();
    loginUserVO.setToken(token);
    loginUserVO.setUserId(userId);
    loginUserVO.setRole(role);
    Map<String, String> stringMap = new HashMap<>();
    stringMap.put("userId", userId.toString());
    stringMap.put("role", role);
    String tokenKey = Common.LOGIN_TOKEN_KEY + token;
    stringRedisTemplate.opsForHash().putAll(tokenKey, stringMap);
    stringRedisTemplate.expire(tokenKey, Common.LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
    //以LOGIN_TOKEN_KEY+userid为key，userId+role序列化map存到redis
    UserHolder.saveUser(loginUserVO);
    return loginUserVO;
  }

  @Async("taskExecutor")
  public void sendCodeForRegister(String email) {
    log.info("尝试发送邮箱验证码给用户：" + email + "进行注册操作");
    log.info("开始发送邮件..." + "获取的到邮件发送对象为:" + mailSender);
    mailUtil = new MailUtil(mailSender, fromEmail);
    String code = mailUtil.sendCode(email);
    //验证码存入redis，有效期1min,用注册的邮箱区分验证码
    stringRedisTemplate.opsForValue()
        .set(USER_REGISTER_CODE_KEY + email, code, REGISTER_CODE_TTL, TimeUnit.MINUTES);
    log.info("发送邮箱验证码给用户：" + email + "成功 : " + code);
  }

  /**
   * @param registerCodeRequest
   */
  @Override
  public void sendRegisterCode(RegisterCodeRequest registerCodeRequest) {
    String mail = registerCodeRequest.getMail();
    //检查该邮箱是否已注册
    checkMailIsRegistered(mail);
    //发送验证码
    sendCodeForRegister(mail);
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
      throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.LOGIN_INFO_ERROR);
    }

    if (!password.equals(rePassword) || checkMail(mail) || checkUserName(userName) || checkPassword(
        password)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.LOGIN_INFO_ERROR);
    }

    //检查邮箱是否被注册
    checkMailIsRegistered(mail);
    //从redis获取验证码
    String rightCode = stringRedisTemplate.opsForValue().get(USER_REGISTER_CODE_KEY + mail);
    //检查验证码是否存在
    if (StringUtils.isBlank(rightCode)) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码已过期或不存在");
    }
    //核验验证码是否正确
    if (!rightCode.equals(registerCode)) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码错误");
    }
    //4.密码盐值加密，写入数据库，注册成功
    String bcrypt = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
    HttpServletRequest request = requestAttributes.getRequest();
    String ipAddr = IPUtil.getIpAddr(request);
    log.info("用户注册，ip地址：{}", ipAddr);
    String ipRegion = IPUtil.getIpRegion(ipAddr);
    log.info("用户注册，ip归属地：{}", ipRegion);
    User user = User.builder().userName(userName).password(bcrypt).mail(mail).ipAddress(ipAddr)
        .build();
    int insert = userMapper.insert(user);
    if (insert <= 0) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.ADD_ERROR);
    }
    return loginSuccess(userMapper.getUserRole(user.getUserId()),user.getUserId());
  }

  private void checkMailIsRegistered(String mail) {
    //发送验证码时已检查该邮箱是否注册，这里再检查一次
    LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
    userLambdaUpdateWrapper.eq(User::getMail, mail);
    if (userMapper.exists(userLambdaUpdateWrapper)) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.MAIL_EXISTED_ERROR);
    }
  }

  //邮箱格式校验
  private boolean checkMail(String mail) {
    return !Pattern.compile(EMAIL_REGEX).matcher(mail).matches();
  }

  //用户名格式校验
  private boolean checkUserName(String userName) {
    return !Pattern.compile(USERNAME_REGEX).matcher(userName).matches();
  }

  //密码格式校验
  private boolean checkPassword(String password) {
    return !Pattern.compile(PASSWORD_REGEX).matcher(password).matches();
  }


  /**
   * @return
   */
  @Override
  public Boolean logout(HttpServletRequest httpServletRequest) {

    //获取请求头中的token，这是用户登录时生成的uuid
    String token = httpServletRequest.getHeader("authorization");
    if (StringUtils.isBlank(token)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "authorization字段token为空");
    }
//        Long userId = UserHolder.getUser().getUserId();
    String tokenKey = Common.LOGIN_TOKEN_KEY + token;
    Boolean delete = stringRedisTemplate.delete(tokenKey);
    if (Boolean.TRUE.equals(delete)) {
      UserHolder.removeUser();
      return true;
    }
    return false;

  }


  @Override
  public LoginUserVO getLoginUser() {

    log.info("获取登录用户线程：" + Thread.currentThread().getId());
    LoginUserVO loginUserVO = UserHolder.getUser();
    log.info("获取登陆用户信息：" + loginUserVO);
    if (loginUserVO == null) {
      throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "获取当前登录用户失败");
    }
    Long userId = loginUserVO.getUserId();
    //获取数据库最新的数据，防止用户更新完个人信息后拿到的还是老数据
    User user = userMapper.selectById(userId);
    LoginUserVO loginUserVO1 = new LoginUserVO();
    BeanUtils.copyProperties(user, loginUserVO1);
    String ipAddress = user.getIpAddress();
    String ipRegion = IPUtil.getIpRegion(ipAddress);
    loginUserVO1.setIpAddress(ipRegion);
    String interestTag = user.getInterestTag();
    if (StringUtils.isNotBlank(interestTag)) {
      List<Long> tagIdlist = JSONUtil.toList(JSONUtil.parseArray(interestTag), Long.class);
      if (tagIdlist != null) {
        List<Tags> tags = tagsMapper.selectBatchIds(tagIdlist);
        List<String> tagNameList = tags.stream().map(Tags::getTagName).collect(Collectors.toList());
        loginUserVO1.setInterestTag(tagNameList);
      }

    }
    return loginUserVO1;
  }


  @Override
  public Page<List<AdminUserVO>> getUserList(AdminUserQueryPageRequest adminUserQueryPageRequest) {
    int currentPage = adminUserQueryPageRequest.getCurrentPage();
    int pageSize = adminUserQueryPageRequest.getPageSize();
    String userName = adminUserQueryPageRequest.getUserName();
    String mail = adminUserQueryPageRequest.getMail();
    Date endTime = adminUserQueryPageRequest.getEndTime();
    Date startTime = adminUserQueryPageRequest.getStartTime();
    Long userId = adminUserQueryPageRequest.getUserId();
    Page<User> userPage = new Page<>(currentPage, pageSize);
    Page<User> userDesc = page(userPage,
        new LambdaQueryWrapper<User>().orderByDesc(User::getCreateTime)
            .gt(startTime != null, User::getCreateTime, startTime)
            .lt(endTime != null, User::getCreateTime, endTime)
            .eq(userId != null, User::getUserId, userId)
            .eq(StringUtils.isNotBlank(mail), User::getMail, mail)
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
    List<AdminUserVO> adminUserVOListByUserList = getAdminUserVOListByUserList(records);
    //包装成单一的list
    listPage.setRecords(Collections.singletonList(adminUserVOListByUserList));
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
          List<Tags> tags = tagsMapper.selectBatchIds(tagIdList);
          List<String> tagNameList = tags.stream().map(Tags::getTagName)
              .collect(Collectors.toList());
          adminUserVO.setInterestTag(tagNameList);
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
        List<Tags> tags = tagsMapper.selectBatchIds(tagIdList);
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
  public Boolean disableUser(Long userId) {
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
    Long userId = UserHolder.getUser().getUserId();
    user.setUserId(userId);
    BeanUtil.copyProperties(updateUserDTO, user);
//    updateUserDTO.getInterestTag()
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
    //初始密码可以在yml中设置
    String bcrypt = DigestUtils.md5DigestAsHex((SALT + originPassword).getBytes());
    User addUser = new User();
    BeanUtil.copyProperties(addUserDTO, addUser);
    addUser.setPassword(bcrypt);
    boolean save = this.save(addUser);
    if (save) {
      return addUser.getUserId();
    }
    throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.ADD_ERROR);
  }


}




