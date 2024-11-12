package com.serein.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.constants.ErrorInfo;
import com.serein.constants.Common;
import com.serein.constants.UserRole;
import com.serein.mapper.*;
import com.serein.model.Request.LoginRequest;
import com.serein.model.Request.RegisterCodeRequest;
import com.serein.model.Request.RegisterRequest;
import com.serein.model.UserHolder;
import com.serein.model.dto.userDTO.UpdateUserDTO;
import com.serein.model.dto.userDTO.AddUserDTO;
import com.serein.model.entity.*;
import com.serein.model.vo.PassageVO.PassageVO;
import com.serein.model.vo.UserVO.AdminUserVO;
import com.serein.model.vo.UserVO.LoginUserVO;
import com.serein.model.vo.UserVO.UserVO;
import com.serein.exception.BusinessException;
import com.serein.service.UserService;
import com.serein.constants.ErrorCode;
import com.serein.utils.BaseResponse;
import com.serein.utils.JwtHelper;

import com.serein.utils.MailUtils;
import com.serein.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.serein.constants.Common.*;

/**
* @author 懒大王Smile
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-09-12 22:19:13
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    //盐值.从yml文件获取
    @Value("${custom.salt}")
    String SALT;

    @Value("${custom.originPassword}")
    String originPassword;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    JwtHelper jwtHelper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserCollectsMapper userCollectsMapper;

    @Autowired
    UserThumbsMapper userThumbsMapper;

    @Autowired
    PassageServiceImpl passageService;



    @Autowired
    UserFollowMapper userFollowMapper;

    @Resource
    protected JavaMailSenderImpl mailSender;
    protected MailUtils mailUtils;

    @Autowired
    PassageMapper passageMapper;


    /**
     * 关注或取关
     * @param userId
     * @return
     * @Description: 用户的关注信息存在redis中，登录用户的Id为key，被关注的用户Id为value
     */
    @Override
    public Boolean follow(Long userId) {
        //todo 也可以存在mysql
        LoginUserVO loginUserVO = UserHolder.getUser();
        if(loginUserVO==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,ErrorInfo.NOT_LOGIN_ERROR);
        }
        Long loginUserId =loginUserVO.getUserId();
        String key = Common.USER_FOLLOW_KEY + userId;
        //使用 stringRedisTemplate.opsForZSet().score(key, loginUserId.toString()) 查询当前登录用户是否已经关注了目标用户。
        // 如果返回值为 null，表示用户未关注目标用户；如果返回一个非 null 的分数，表示已经关注。
        Double score = stringRedisTemplate.opsForZSet().score(key, loginUserId.toString());
        if (score==null){
            //如果用户未关注目标用户，执行关注操作:
            UserFollow userFollow = UserFollow.builder().userId(loginUserId).toUserId(userId).build();
            //先更新数据库 user-follow表
            int insert = userFollowMapper.insert(userFollow);
            if (insert==1){
                //再更新redis
                stringRedisTemplate.opsForZSet().add(key, loginUserId.toString(), System.currentTimeMillis());
            }else {
                throw new BusinessException(ErrorCode.OPERATION_ERROR,ErrorInfo.UPDATE_ERROR);
            }
        }else {
            //如果用户已经关注目标用户，执行取消关注操作:
            QueryWrapper<UserFollow> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId",loginUserId).eq("toUserId",userId);
            int delete = userFollowMapper.delete(queryWrapper);
            if (delete==1){
                stringRedisTemplate.opsForZSet().remove(key,loginUserId.toString());
            }else {
                throw new BusinessException(ErrorCode.OPERATION_ERROR,ErrorInfo.UPDATE_ERROR);
            }
        }
        return true;
    }

    /**
     * 我关注的用户列表
     * @return
     * @Description: 关注列表存在mysql，可以从redis查，也可以从mysql查
     */
    @Override
    public List<UserVO> myFollow() {
//        LoginUserVO loginUserVO = UserHolder.getUser();
//        if (loginUserVO==null){
//            //未登录直接返回空列表
//            return Collections.emptyList();
//        }
//        Long userId = loginUserVO.getUserId();
        Long userId = checkIsLogin();
        QueryWrapper<UserFollow> queryWrapper = new QueryWrapper<>();
        //从数据库查，可以改成从redis查
        queryWrapper.eq("userId",userId);
        List<UserFollow> userFollows = userFollowMapper.selectList(queryWrapper);
        if (CollUtil.isNotEmpty(userFollows)){
            ArrayList<Long> idList = new ArrayList<>();
            userFollows.forEach(userFollow ->idList.add(userFollow.getToUserId()));
            List<User> userList = this.listByIds(idList);
            List<UserVO> userVOListByUserList = getUserVOListByUserList(userList);
            //我关注的，全部设置成已关注
            userVOListByUserList.forEach(userVO -> userVO.setIsFollow(true));
            return  userVOListByUserList;
        }
        return Collections.emptyList();
    }



    //判断我是否关注了这些用户
    private void isFollow(Long loginUserId,List<UserVO> userVOList){
        for (UserVO userVO : userVOList) {
            String key = Common.USER_FOLLOW_KEY + userVO.getUserId().toString();
            Double score = stringRedisTemplate.opsForZSet().score(key, loginUserId.toString());
            userVO.setIsFollow(score != null);
        }
    }
    
    private Long checkIsLogin(){
        LoginUserVO loginUserVO = UserHolder.getUser();
        if (loginUserVO==null){
            //未登录返回报错，前端显示空数据并提示用户登录
            throw  new BusinessException(ErrorCode.NOT_LOGIN_ERROR,ErrorInfo.NOT_LOGIN_ERROR);
        }
        Long userId = loginUserVO.getUserId();
        return userId;
    }

    /**
     * 我的粉丝列表，要判断是否关注了某些粉丝
     * @return
     * @Description: 以登录用户的 userId为key查redis，也可以查mysql
     */
    @Override
    public List<UserVO> myFollowers() {
//        //redis
//        LoginUserVO loginUserVO = UserHolder.getUser();
//        if (loginUserVO==null){
//            throw  new BusinessException(ErrorCode.NOT_LOGIN_ERROR,ErrorInfo.NOT_LOGIN_ERROR);
//        }
//        Long userId = loginUserVO.getUserId();
        Long userId = checkIsLogin();
        String key = Common.USER_FOLLOW_KEY + userId.toString();
        // 从 Redis 获取当前用户（userId）的所有粉丝 IDs，返回的是一个 Set<String>，其中每个元素是一个粉丝的 ID。
        Set<String> myFollowerIds = stringRedisTemplate.opsForZSet().range(key, 0, -1);
        if (CollUtil.isNotEmpty(myFollowerIds)){
            //拿到idList
            List<Long> list = myFollowerIds.stream().map(followerId -> Long.valueOf(followerId)).collect(Collectors.toList());
            List<User> userList = this.listByIds(list);
            List<UserVO> userVOListByUserList = getUserVOListByUserList(userList);
                //判断我是否关注了粉丝
            isFollow(userId,userVOListByUserList);
            return userVOListByUserList;
        }
        //mysql
        /*QueryWrapper<UserFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("toUserId",userId);
        List<UserFollow> myFollowers = userFollowMapper.selectList(queryWrapper);
        if (CollUtil.isNotEmpty(myFollowers)){
            ArrayList<Long> idList = new ArrayList<>();
            myFollowers.forEach(myFollower ->idList.add(myFollower.getUserId()));
            List<User> userList = this.listByIds(idList);
            List<UserVO> userVOListByUserList = getUserVOListByUserList(userList);
            //判断我是否关注了粉丝
            isFollow(userVOListByUserList);
            return userVOListByUserList;
        }*/
        return Collections.emptyList();
    }

    /**
     *
     * @param uid
     * @return 获取其他用户的信息，展示在其他用户的主页或者文章详情页
     */
    @Override
    public UserVO getUserInfo(Long uid) {
        User byId = this.getById(uid);
        if (byId!=null){
            UserVO userVO = new UserVO();
            BeanUtil.copyProperties(byId,userVO);
            String interestTag = byId.getInterestTag();
            if (StringUtils.isNotBlank(interestTag)){
                userVO.setInterestTag(JSONUtil.toList(interestTag,String.class));
            }
            List<UserVO> userVOS = new ArrayList<>();
            userVOS.add(userVO);
            LoginUserVO loginUserVO = UserHolder.getUser();
            if(loginUserVO!=null)
            {isFollow(loginUserVO.getUserId(),userVOS);}
            return userVO;
        }
        return null;
    }

    @Override
    public Boolean setAdmin(Long userId) {
        User byId = getById(userId);
        String role = byId.getRole();
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        if (role.equals(UserRole.ADMIN_ROLE)){
            userUpdateWrapper.eq("userId",userId).set("role", UserRole.DEFAULT_ROLE);
        }else {
            userUpdateWrapper.eq("userId",userId).set("role",UserRole.ADMIN_ROLE);
        }
        boolean b = this.update(userUpdateWrapper);
        if (b){
            return true;
        }
        return null;
    }


    @Override
    public List<PassageVO> myCollectPassage() {
        LoginUserVO loginUserVO = UserHolder.getUser();
        if (loginUserVO==null){
            //未登录直接返回空列表
            return Collections.emptyList();
        }
        Long userId = loginUserVO.getUserId();
        QueryWrapper<UserCollects> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        List<UserCollects> userCollects = userCollectsMapper.selectList(queryWrapper);
        ArrayList<Long> passageIdList = new ArrayList<>();
        if (CollUtil.isNotEmpty(userCollects)){
            userCollects.forEach(userCollects1 ->
                    passageIdList.add(userCollects1.getPassageId())
            );
        }
        List<Passage> passageList = passageService.listByIds(passageIdList);
        return passageService.getPassageVOList(passageList);
    }

    @Override
    public List<PassageVO> myThumbPassage() {
//        LoginUserVO loginUserVO = UserHolder.getUser();
//        if(loginUserVO==null){
//           return Collections.emptyList();
//        }
//        Long userId = loginUserVO.getUserId();
        Long userId = checkIsLogin();
        QueryWrapper<UserThumbs> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        List<UserThumbs> userThumbs = userThumbsMapper.selectList(queryWrapper);
        ArrayList<Long> passageIdList = new ArrayList<>();
        if (CollUtil.isEmpty(userThumbs)){
            return  Collections.emptyList();
        }
        userThumbs.forEach(userThumbs1 ->
                passageIdList.add(userThumbs1.getPassageId())
        );
        List<Passage> passageList = passageService.listByIds(passageIdList);
        return passageService.getPassageVOList(passageList);
    }

    @Override
    public List<PassageVO> myPassage() {
//        LoginUserVO loginUserVO = UserHolder.getUser();
//        if(loginUserVO==null){
//            return Collections.emptyList();
//        }
//        Long userId = loginUserVO.getUserId();
        Long userId = checkIsLogin();
        QueryWrapper<Passage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        List<Passage> passages = passageMapper.selectList(queryWrapper);
        if(CollUtil.isEmpty(passages)){
            return Collections.emptyList();
        }
        return passageService.getPassageVOList(passages);
    }


    /**
     *
     * @param loginRequest
     * @return
     */
    @Override
    public LoginUserVO login(LoginRequest loginRequest) {

        //1.判断邮箱和密码是否为空,邮箱格式校验，密码长度校验
        String loginUserMail = loginRequest.getMail();
        String loginPassword = loginRequest.getPassword();
        if (StringUtils.isAnyBlank(loginUserMail, loginPassword)|| checkMail(loginUserMail) ||loginPassword.length()<6){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,ErrorInfo.LOGIN_INFO_ERROR);
        }

        //2.根据邮箱从数据库查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mail", loginUserMail);
        User queryUser = userMapper.selectOne(queryWrapper);
        if (queryUser==null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,ErrorInfo.NO_DB_DATA);
        }

        //核对密码
        if (!DigestUtils.md5DigestAsHex((SALT+loginPassword).getBytes()).equals(queryUser.getPassword())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,ErrorInfo.PASSWORD_ERROR);
        }

        LoginUserVO loginUserVO = new LoginUserVO();

        BeanUtil.copyProperties(queryUser,loginUserVO);

        saveUserAndToken(queryUser,loginUserVO);
        log.info("loginUserVO："+loginUserVO);
        log.info("登录线程："+Thread.currentThread().getId());
        return loginUserVO;
    }

    /**
     * 根据uid生成并记录token到redis
     * @param loginUserVO
     */
    private void saveUserAndToken(User user,LoginUserVO loginUserVO) {
        //登录态保存到本地线程
        //UserHolder.saveUser(loginUserVO);

        if (!StringUtils.isBlank(user.getInterestTag())){
            //把数据库中string类型的json转换成list<String>
            List<String> pTagList = JSONUtil.toList(user.getInterestTag(), String.class);
            loginUserVO.setInterestTag(pTagList);
        }

        String token = UUID.randomUUID(true).toString(false);
        //以LOGIN_TOKEN_KEY+userid为key，loginUserVO为值序列化存到redis
        log.info(loginUserVO.getUserId()+"用户的token: "+token);
        loginUserVO.setToken(token);
        Map<String, Object> map = BeanUtil.beanToMap(loginUserVO, new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true).setFieldValueEditor((name, value) -> value.toString()));
        String tokenKey=Common.LOGIN_TOKEN_KEY+token;
        stringRedisTemplate.opsForHash().putAll(tokenKey,map);
        stringRedisTemplate.expire(tokenKey,Common.LOGIN_TOKEN_TTL,TimeUnit.MINUTES);
        //  String token = jwtHelper.createToken(loginUserVO.getUserId());
        //设置token有效期10min，用户进行操作时会刷新redis的token有效期
    }

    private void sendCodeForRegister( String email) {
        log.info("尝试发送邮箱验证码给用户：" + email + "进行注册操作");
        log.info("开始发送邮件..." + "获取的到邮件发送对象为:" + mailSender);
        mailUtils = new MailUtils(mailSender, fromEmail);
        String code = mailUtils.sendCode(email);
        //验证码存入redis，有效期1min,用注册的邮箱区分验证码
        stringRedisTemplate.opsForValue().set(USER_REGISTER_CODE_KEY+email,code,REGISTER_CODE_TTL,TimeUnit.MINUTES);
        log.info("发送邮箱验证码给用户：" + email + "成功 : "+code);
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
     *
     * @param registerRequest
     * @return
     */
    @Override
    public  LoginUserVO register(RegisterRequest registerRequest) {

        String mail = registerRequest.getMail();
        String password = registerRequest.getPassword();
        String rePassword = registerRequest.getRePassword();
        String userName = registerRequest.getUserName();
        String registerCode = registerRequest.getCode();

        //检查注册参数是否为空
        if (StringUtils.isAnyBlank(mail,password,rePassword,userName,registerCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,ErrorInfo.LOGIN_INFO_ERROR);
        }
        //用户名长度不能<2，密码长度不能<6
        if (!password.equals(rePassword)|| checkMail(mail)|| userName.length()>2||password.length()<6){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,ErrorInfo.LOGIN_INFO_ERROR);
        }

        //检查邮箱是否被注册
        checkMailIsRegistered(mail);
        //从redis获取验证码
        String rightCode = stringRedisTemplate.opsForValue().get(USER_REGISTER_CODE_KEY + mail);
        //检查验证码是否存在
        if (StringUtils.isBlank(rightCode)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"验证码已过期或不存在");
        }
        //核验验证码是否正确
        if (!rightCode.equals(registerCode)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"验证码错误");
        }
        //4.密码盐值加密，写入数据库，注册成功
        String bcrypt =DigestUtils.md5DigestAsHex((SALT+password).getBytes());
        User user =User.builder().userName(userName).password(bcrypt).mail(mail).build();
        int insert = userMapper.insert(user);
        if (insert<=0){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,ErrorInfo.ADD_ERROR);
        }
        //根据Userid从数据库查出用户信息并返回VO
        user = this.getById(user.getUserId());
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user,loginUserVO);
        log.info("注册成功："+loginUserVO);
        saveUserAndToken(user,loginUserVO);
        return loginUserVO;
    }

    private void checkMailIsRegistered(String mail) {
        //发送验证码时已检查该邮箱是否注册，这里再检查一次
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mail", mail);
        if (userMapper.exists(queryWrapper)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,ErrorInfo.MAIL_EXISTED_ERROR);
        }
    }

    //邮箱格式校验
    private boolean checkMail(String mail) {
        return !Pattern.compile(EMAIL_REGEX).matcher(mail).matches();
    }

    /**
     *
     * @return
     */
    @Override
    public Boolean logout(HttpServletRequest httpServletRequest) {

        //获取请求头中的token，这是用户登录时生成的uuid
        String token = httpServletRequest.getHeader("authorization");
        if (StringUtils.isBlank(token)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"authorization字段token为空");
        }
//        Long userId = UserHolder.getUser().getUserId();
        String tokenKey=Common.LOGIN_TOKEN_KEY+token;
        Boolean delete = stringRedisTemplate.delete(tokenKey);
        if (Boolean.TRUE.equals(delete)){
            UserHolder.removeUser();
            return true;
        }
        return false;

    }


    @Override
    public LoginUserVO getLoginUser() {

        log.info("获取登录用户线程："+Thread.currentThread().getId());
        LoginUserVO loginUserVO = UserHolder.getUser();
        if (loginUserVO==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"获取当前登录用户失败");
        }
        return loginUserVO;
    }


    @Override
    public List<AdminUserVO> getUserList(Long current) {
        Page<User> page =query()
                .page(new Page<>(current, Common.PAGE_SIZE));
        List<User> userList = page.getRecords();
        if (userList.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"查询用户列表失败");
        }
        return getAdminUserVOListByUserList(userList);
    }

    @Override
    public List<UserVO> getUserListByName(String userName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName",userName).eq("status",1);
        List<User> userList = this.list(queryWrapper);
        if (userList.isEmpty()){
            return Collections.emptyList();
        }
        return getUserVOListByUserList(userList);
    }


    public List<AdminUserVO> getAdminUserVOListByUserList(List<User> userList){
        return userList.stream().map(user -> {
            AdminUserVO adminUserVO = new AdminUserVO();
            BeanUtils.copyProperties(user, adminUserVO);
            if (StringUtils.isNotBlank(user.getInterestTag())){
                List<String> list = JSONUtil.toList(user.getInterestTag(), String.class);
                adminUserVO.setInterestTag(list);
            }
            return adminUserVO;
        }).collect(Collectors.toList());
    }

    /**
     * 把userList转成userVOList
     * @param userList
     * @return
     */
    public List<UserVO> getUserVOListByUserList(List<User> userList){
        return userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            if (StringUtils.isNotBlank(user.getInterestTag())){
                List<String> list = JSONUtil.toList(user.getInterestTag(), String.class);
                userVO.setInterestTag(list);
            }
            return userVO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AdminUserVO> getByIdList(List<Long> idList) {

        List<User> userList = this.listByIds(idList);
        if (userList.isEmpty()){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,ErrorInfo.NO_DB_DATA);
        }
        return getAdminUserVOListByUserList(userList);
    }


    @Override
    public Boolean disableUser(Long userId) {
        User byId = getById(userId);
        Integer status = byId.getStatus();
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        if (status==0){
            userUpdateWrapper.eq("userId",userId).set("status",1);
        }else {
            userUpdateWrapper.eq("userId",userId).set("status",0);
        }
        boolean b = this.update(userUpdateWrapper);
        if (b){
            return true;
        }
        throw new BusinessException(ErrorCode.OPERATION_ERROR,ErrorInfo.UPDATE_ERROR);
    }

    @Override
    public Boolean updateUser(UpdateUserDTO updateUserDTO) {
        User user = new User();
        Long userId = UserHolder.getUser().getUserId();
        user.setUserId(userId);
        BeanUtil.copyProperties(updateUserDTO,user);
        boolean b = this.updateById(user);
        if (!b){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,ErrorInfo.UPDATE_ERROR);
        }
        return true;
    }

    /**
     * 管理添加用户时可以设置密码
     * @param addUserDTO
     * @return
     */
    @Override
    public Long addUser(AddUserDTO addUserDTO) {
        //初始密码可以在yml中设置
        String bcrypt =DigestUtils.md5DigestAsHex((SALT+originPassword).getBytes());
        User addUser = new User();
        BeanUtil.copyProperties(addUserDTO,addUser);
        addUser.setPassword(bcrypt);
        boolean save = this.save(addUser);
        if (save){
            return addUser.getUserId();
        }
        throw new BusinessException(ErrorCode.OPERATION_ERROR,ErrorInfo.ADD_ERROR);
    }




}




