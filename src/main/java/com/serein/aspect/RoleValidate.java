package com.serein.aspect;

import com.serein.annotation.AuthCheck;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.exception.BusinessException;
import com.serein.util.UserHolder;
import com.serein.model.enums.UserRoleEnum;
import com.serein.model.vo.userVO.LoginUserVO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/14
 * @Time: 15:31
 * @Description: 权限校验的切面
 */


@Aspect
@Component
public class RoleValidate {

  @Around("@annotation(authCheck)")
  public Object roleValidate(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
    /**
     * 1.获取访问该方法所需的权限mustRole
     * 如果mustRole为空说明访问该方法不需要权限，直接放行，如果为空，则检查用户的权限
     * 2.获取当前登录用户的权限与mustRole比对
     */
    String mustRole = authCheck.mustRole();
    UserRoleEnum mustUserRoleEnum = UserRoleEnum.getEnumByRole(mustRole);
    if (mustUserRoleEnum == null) {
      //该方法不需要任何权限就能访问，放行
      return joinPoint.proceed();
    }
    LoginUserVO loginUserVO = UserHolder.getUser();
    //对于某些接口未登录用户不能访问
    if (loginUserVO == null) {
      throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, ErrorInfo.NOT_LOGIN_ERROR);
    }
    //获取登录用户的role
    String loginUserRole = loginUserVO.getRole();
    UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByRole(loginUserRole);
    if (UserRoleEnum.ADMIN.equals(mustUserRoleEnum)) {
      //若该方法需要admin权限而用户没有，拒绝
      if (!UserRoleEnum.ADMIN.equals(userRoleEnum)) {
        throw new BusinessException(ErrorCode.NO_AUTH_ERROR, ErrorInfo.NO_AUTH_ERROR);
      }
    }
    return joinPoint.proceed();
  }
}
