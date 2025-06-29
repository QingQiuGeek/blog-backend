package com.serein.interceptor;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import cn.hutool.core.bean.BeanUtil;
import com.serein.constants.Common;
import com.serein.util.UserContext;
import com.serein.model.vo.userVO.LoginUserVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/14
 * @Time: 18:24
 * @Description: 该拦截器只负责刷新token（redis共享session），不负责拦截
 */
@Deprecated
@Component
public class RefreshTokenInterceptor implements HandlerInterceptor {

  @Resource
  StringRedisTemplate stringRedisTemplate;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    //前端请求时带上authorization
    String token = request.getHeader(AUTHORIZATION);
    if (StringUtils.isBlank(token)) {
      //未登录，直接放行，由登录拦截器拦截
      return true;
    }

    //从redis获取token
    String tokenKey = Common.LOGIN_TOKEN_KEY + token;
    Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(tokenKey);
    if (map.isEmpty()) {
      //redis中存储的登录态已失效，放行，让登录拦截器拦截
      return true;
    }
//    LoginUserVO loginUserVO = BeanUtil.fillBeanWithMap(map, new LoginUserVO(), false);
    //将用户信息保存到ThreadLocal中
//    UserContext.saveUser(loginUserVO);

    //刷新redis的token有效期
    stringRedisTemplate.expire(tokenKey, Common.LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
    return true;
  }


  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) {
    //// 移除用户,防止内存泄漏!!!
    UserContext.removeUser();
  }
}
