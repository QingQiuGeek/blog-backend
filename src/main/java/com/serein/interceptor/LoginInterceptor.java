package com.serein.interceptor;

import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.exception.BusinessException;
import com.serein.model.UserHolder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/14
 * @Time: 18:07
 * @Description:
 */

@Component
public class LoginInterceptor implements HandlerInterceptor {

  /*
   * authorization为空和redis的token失效的都放行到登录拦截器
   * */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    if (UserHolder.getUser() == null) {
      response.setStatus(401);
      //response.setHeader("登录拦截器：","该请求被拦截，请登录！");
      throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, ErrorInfo.NOT_LOGIN_ERROR);
    }
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {
    HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
  }
}
