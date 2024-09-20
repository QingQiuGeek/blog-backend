package com.serein.aspect;

import com.serein.annotation.AuthCheck;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.domain.dto.LoginUserDTO;
import com.serein.domain.entity.User;
import com.serein.exception.BusinessException;
import com.serein.service.UserService;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/14
 * @Time: 15:31
 * @Description:
 */


@Aspect
@Component
public class RoleValidateAOP {

    @Autowired
    UserService userService;

    @Around("@annotation(authCheck)")
    public Object RoleValidate(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {

        /**
         *
         * 1.获取系统要求权限 authCheck
         * 如果系统authCheck为空说明不需要权限，直接放行
         * 2.根据httpServletRequest 获取 当前登录用户及权限
         * 3.检查权限是否符合authCheck
         *
         */

        String mustRole = authCheck.mustRole();
        if (StringUtils.isBlank(mustRole)){
           return joinPoint.proceed();
        }

        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        LoginUserDTO loginUser = userService.getLoginUser();

        String userRole = loginUser.getRole().toString();
        if (StringUtils.isBlank(userRole)){
            throw new BusinessException(ErrorCode.NO_AUTH, ErrorInfo.NO_AUTH_ERROR);
        }
        if (userRole.equals(mustRole)){
            return joinPoint.proceed();
        }

        throw new BusinessException(ErrorCode.NO_AUTH, ErrorInfo.NO_AUTH_ERROR);

    }
}
