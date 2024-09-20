package com.serein.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.constants.Common;
import com.serein.domain.CustomPage;
import com.serein.domain.Request.LoginRequest;
import com.serein.domain.Request.RegisterRequest;
import com.serein.domain.UserHolder;
import com.serein.domain.dto.LoginUserDTO;
import com.serein.domain.dto.UserDTO;
import com.serein.domain.entity.User;
import com.serein.domain.vo.UserVO;
import com.serein.exception.BusinessException;
import com.serein.service.UserService;
import com.serein.mapper.UserMapper;
import com.serein.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     *
     * @param loginRequest
     * @param httpServletRequest
     * @return
     */
    @Override
    public ResultUtils login(LoginRequest loginRequest, HttpServletRequest httpServletRequest) {
        //用户可能在已经登陆的情况下再次登录，那么此时要根据之前的token删除redis中已经存在的用户信息，减少内存开销
        //authorization是前端发请求时设置的
        String preToken = httpServletRequest.getHeader("authorization");
        if (!StringUtils.isBlank(preToken)){
            log.info("之前的token： "+preToken);
            String tokenKey=Common.LOGIN_TOKEN_KEY+preToken;
            Boolean delete = stringRedisTemplate.delete(tokenKey);
            if (Boolean.FALSE.equals(delete)){
                throw new BusinessException(ErrorCode.UNEXPECTED_ERROR,"旧token数据删除失败");
            }
        }

        if(loginRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.PARAMS_ERROR);
        }
        //1.判断用户名密码是否为空
        String loginUserAccount = loginRequest.getUserAccount();
        String loginPassword = loginRequest.getPassword();
        if (StringUtils.isAllBlank(loginUserAccount, loginPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,ErrorInfo.LOGIN_INFO_ERROR);
        }

        //2.根据用户名从数据库查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", loginUserAccount);
        User queryUser = this.baseMapper.selectOne(queryWrapper);
        if (queryUser==null){
            throw new BusinessException(ErrorCode.NO_DATA,ErrorInfo.NO_DB_DATA);
        }

        //核对密码
        if (!DigestUtils.md5DigestAsHex((SALT+loginPassword).getBytes()).equals(queryUser.getPassword())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,ErrorInfo.PASSWORD_ERROR);
        }

        LoginUserDTO userDTO = new LoginUserDTO();
        BeanUtil.copyProperties(queryUser,userDTO);

        //登录态保存到session 保存在服务器占内存！！！后续保存到redis
//        httpServletRequest.getSession().setAttribute(Common.USER_LOGIN_STATE,userDTO);

        //利用redis存储session会话，实现session共享
        //token就相当于sessionID
        String token = UUID.randomUUID().toString(false);
        log.info(loginRequest.getUserAccount()+"用户的token: "+token);
        Map<String, Object> map = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true).setFieldValueEditor((name, value) -> value.toString()));
        String tokenKey=Common.LOGIN_TOKEN_KEY+token;
        stringRedisTemplate.opsForHash().putAll(tokenKey,map);
        stringRedisTemplate.expire(tokenKey,Common.LOGIN_TOKEN_TTL, TimeUnit.MINUTES);

        UserHolder.saveUser(userDTO);

        log.info("userDTO："+UserHolder.getUser().toString());
        log.info("登录线程："+Thread.currentThread().getId());

        //返回token（sessionID）
        return ResultUtils.ok("登陆成功",token);

    }

    /**
     *
     * @param registerRequest
     * @param httpServletRequest
     * @return
     */
    @Override
    public ResultUtils register(RegisterRequest registerRequest, HttpServletRequest httpServletRequest) {

        String RegUserAccount = registerRequest.getUserAccount();
        String Regpassword = registerRequest.getPassword();
        //1.检查参数是否为null,密码长度应>=6
        if (StringUtils.isAnyBlank(Regpassword,RegUserAccount)||Regpassword.length()<6){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,ErrorInfo.LOGIN_INFO_ERROR);
        }
        //3.检查 账户名 是否重复
        IsUserAccountRepeat(RegUserAccount);

        //4.密码盐值，加密，写入数据库，注册成功
        String bcrypt =DigestUtils.md5DigestAsHex((SALT+Regpassword).getBytes());
        User user =User.builder().userName("serein").userAccount(RegUserAccount).password(bcrypt).mail(registerRequest.getMail()).build();

        if (StringUtils.isNotBlank(registerRequest.getUserName())){
            user.setUserName(registerRequest.getUserName());
        }

        this.baseMapper.insert(user);
        return ResultUtils.ok("注册成功");
    }

    /**
     *
     * @param httpServletRequest
     * @return
     */
    @Override
    public ResultUtils logout(HttpServletRequest httpServletRequest) {
//        if (httpServletRequest.getSession().getAttribute(Common.USER_LOGIN_STATE)==null){
////            printSession(httpServletRequest);
//            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,ErrorInfo.NOT_LOGIN_ERROR);
//        }
//        httpServletRequest.getSession().removeAttribute(common.USER_LOGIN_STATE);
//        httpServletRequest.getSession().invalidate();//直接清空会话

        //获取请求头中的token，这是用户登录时生成的uuid
        String token = httpServletRequest.getHeader("authorization");
        if (StringUtils.isBlank(token)){
            throw new BusinessException(ErrorCode.UNEXPECTED_ERROR,"请求头authorization字段token为空");
        }
        String tokenKey=Common.LOGIN_TOKEN_KEY+token;
        Boolean delete = stringRedisTemplate.delete(tokenKey);
        if (Boolean.TRUE.equals(delete)){
            UserHolder.removeUser();
            return ResultUtils.ok("退出登录成功");
        }
        return ResultUtils.ok("已退出登录，不要重试！！");

    }


    @Override
    public LoginUserDTO getLoginUser() {

        log.info("获取登录用户线程："+Thread.currentThread().getId());
        LoginUserDTO loginUserDTO = UserHolder.getUser();
        if (loginUserDTO==null){
            throw new BusinessException(ErrorCode.UNEXPECTED_ERROR,"获取当前登录用户失败");
        }
        return loginUserDTO;
    }


    @Override
    public ResultUtils getUserList(Long current) {
        Page<User> page =query()
                .page(new Page<>(current, Common.PAGE_SIZE));
        List<User> userList = page.getRecords();
        if (userList.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"查询用户列表失败");
        }
        List<UserVO> userVOList = getVOListByUserList(userList);
//        return ResultUtils.ok("查询用户列表成功",page);
        log.info("total: "+userVOList.size());
        return ResultUtils.ok("查询用户列表成功",new CustomPage<UserVO>(current,Common.PAGE_SIZE,page.getTotal(),userVOList),page.getTotal());
    }

    @Override
    public ResultUtils getByUserName(String userName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName",userName).eq("status",1);
        List<User> userList = this.list(queryWrapper);
        if (userList.isEmpty()){
            throw new BusinessException(ErrorCode.NO_DATA,ErrorInfo.NO_DB_DATA);
        }
        List<UserVO> userVOList = getVOListByUserList(userList);
        return ResultUtils.ok("根据用户名查询用户成功",userVOList,Long.valueOf(userVOList.size()));
    }


    public List<UserVO> getVOListByUserList(List<User> userList){
        return userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
    }

    @Override
    public ResultUtils getByIdList(List<Integer> idList) {

        List<User> userList = this.listByIds(idList);
        if (userList.isEmpty()){
            throw new BusinessException(ErrorCode.NO_DATA,ErrorInfo.NO_DB_DATA);
        }
        List<UserVO> userVOList = getVOListByUserList(userList);
        return ResultUtils.ok("根据id列表查询用户成功",userVOList,Long.valueOf(userVOList.size()));
    }


    @Override
    public ResultUtils disableUser(Long userId) {
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("userId",userId).set("status",0);
        boolean b = this.update(userUpdateWrapper);
        if (b){
            return ResultUtils.ok("管理员禁用用户成功");
        }
        throw new BusinessException(ErrorCode.UPDATE_ERROR,ErrorInfo.UPDATE_ERROR);
    }

    @Override
    public ResultUtils updateUser(UserDTO updateUserDTO) {
        User updateUser = new User();
        BeanUtil.copyProperties(updateUserDTO,updateUser);
        boolean b = this.saveOrUpdate(updateUser);

        if (b){
            return ResultUtils.ok("用户信息更新成功");
        }
        throw new BusinessException(ErrorCode.UPDATE_ERROR,ErrorInfo.UPDATE_ERROR);
    }

    @Override
    public ResultUtils addUser(UserDTO addUserDTO) {
        IsUserAccountRepeat(addUserDTO.getUserAccount());
        String bcrypt =DigestUtils.md5DigestAsHex((SALT+addUserDTO.getPassword()).getBytes());
        User addUser = new User();
        BeanUtil.copyProperties(addUserDTO,addUser);
        addUser.setPassword(bcrypt);
        boolean save = this.save(addUser);
        if (save){
            return ResultUtils.ok("添加新用户成功");
        }
        throw new BusinessException(ErrorCode.ADD_ERROR,ErrorInfo.ADD_ERROR);
    }


    //检查userAccount是否重复
    private void IsUserAccountRepeat(String userAccount){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        boolean exists= this.baseMapper.exists(queryWrapper);
        if (exists){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,ErrorInfo.USERNAME_EXISTED_ERROR);
        }
    }

    /**
     *
     * @param httpServletRequest
     */
    private void printSession(HttpServletRequest httpServletRequest){
        // 获取所有会话属性的名称
        Enumeration<String> attributeNames = httpServletRequest.getSession().getAttributeNames();
        if (!attributeNames.hasMoreElements()){
            log.info("session内容为空");
            return;
        }
        // 打印每个属性的名称和值
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            Object attributeValue = httpServletRequest.getSession().getAttribute(attributeName);
            System.out.println("session内容："+attributeName + ": " + attributeValue);
        }
    }


}




