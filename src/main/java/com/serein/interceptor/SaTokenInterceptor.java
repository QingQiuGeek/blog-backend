package com.serein.interceptor;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import cn.dev33.satoken.stp.StpUtil;
import com.serein.model.enums.UserRoleEnum;
import com.serein.util.UserContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Author: QingQiu
 * @Date: 2025/6/28
 * @Description: 基于sa-token实现的拦截器，实现了token刷新、登录校验
 */
@Slf4j
@Component
public class SaTokenInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String requestURI = request.getRequestURI();
    if (requestURI.contains("/api/webjars") || requestURI.contains("/api/favicon.ico") || requestURI.contains("/api-docs") || requestURI.contains("/error")) {
      return true;
    }

    //刷新token有效期（这一步已经判断了名为authorization的token是否是真实有效的，如果是伪造或过期的token则不会刷新token，报错）
    Long userId;
    try {
      userId = Long.valueOf(StpUtil.getLoginId().toString());
    } catch (Exception e) {
      if(requestURI.contains("/ai")){
        return true;
      }
      throw new RuntimeException(e);
    }

    //虽然每次可以从stpUtil.getLoginId()获取userId，但是这样要读redis，会对其造成压力，因此这里取出来放到userContext，用的时候从userContext取
    UserContext.saveUser(userId);
    //角色校验
    if(requestURI.contains("/admin")){
      StpUtil.checkRole(UserRoleEnum.ADMIN.getRole());
    }
    return true;
  }

  // 移除用户,防止内存泄漏!!!
  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) {
    UserContext.removeUser();
  }

}
