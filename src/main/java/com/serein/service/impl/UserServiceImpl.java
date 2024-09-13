package com.serein.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.constants.common;
import com.serein.domain.dto.LoginUserDTO;
import com.serein.domain.entity.User;
import com.serein.domain.vo.UserVO;
import com.serein.exception.BusinessException;
import com.serein.service.UserService;
import com.serein.mapper.UserMapper;
import com.serein.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
* @author 懒大王Smile
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-09-12 22:19:13
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    //盐值
    @Value("${custom.salt}")
    String SALT;

    /**
     *
     * @param loginUserDto
     * @param httpServletRequest
     * @return
     */
    @Override
    public ResultUtils login(LoginUserDTO loginUserDto, HttpServletRequest httpServletRequest) {

        if(loginUserDto==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.PARAMS_ERROR);
        }
        //1.判断用户名密码是否为空
        String loginUserName = loginUserDto.getUserName();
        String loginPassword = loginUserDto.getPassword();
        if (StringUtils.isAllBlank(loginUserName, loginPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,ErrorInfo.LOGIN_INFO_ERROR);
        }

        //2.根据用户名从数据库查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName", loginUserName);
        User queryUser = this.baseMapper.selectOne(queryWrapper);
        if (queryUser==null){
            throw new BusinessException(ErrorCode.NO_DATA,ErrorInfo.NO_USER_ERROR);
        }

        //核对密码
        if (!DigestUtils.md5DigestAsHex((SALT+loginPassword).getBytes()).equals(queryUser.getPassword())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,ErrorInfo.PASSWORD_ERROR);
        }

        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(queryUser,userVO);
        //保存到session
        httpServletRequest.getSession().setAttribute(common.USER_LOGIN_STATE,userVO);
        printSession(httpServletRequest);

        return ResultUtils.ok("登陆成功",userVO);
    }

    /**
     *
     * @param loginUserDTO
     * @param httpServletRequest
     * @return
     */
    @Override
    public ResultUtils register(LoginUserDTO loginUserDTO, HttpServletRequest httpServletRequest) {

        String RegUserName = loginUserDTO.getUserName();
        String Regpassword = loginUserDTO.getPassword();
        //1.检查参数是否为null,密码长度应>=6
        if (StringUtils.isAnyBlank(Regpassword,RegUserName)||Regpassword.length()<6){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,ErrorInfo.LOGIN_INFO_ERROR);
        }
        //3.检查用户名是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName",RegUserName);
        boolean exists= this.baseMapper.exists(queryWrapper);
        if (exists){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,ErrorInfo.USERNAME_EXISTED_ERROR);
        }
        //4.密码盐值，加密，写入数据库，注册成功
        String bcrypt =DigestUtils.md5DigestAsHex((SALT+Regpassword).getBytes());
        User user =User.builder().userName(RegUserName).password(bcrypt).mail(loginUserDTO.getMail()).build();
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
        if (httpServletRequest.getSession().getAttribute(common.USER_LOGIN_STATE)==null){
            printSession(httpServletRequest);
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,ErrorInfo.NOT_LOGIN_ERROR);
        }
        printSession(httpServletRequest);
        httpServletRequest.getSession().removeAttribute(common.USER_LOGIN_STATE);
        printSession(httpServletRequest);
        return ResultUtils.ok("已退出登录");
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




