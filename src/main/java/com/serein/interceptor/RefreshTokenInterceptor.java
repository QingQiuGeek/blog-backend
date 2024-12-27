package com.serein.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.serein.constants.Common;
import com.serein.util.UserHolder;
import com.serein.model.vo.userVO.LoginUserVO;
import com.serein.util.JwtHelper;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/14
 * @Time: 18:24
 * @Description: 该拦截器只负责刷新token（redis共享session），不负责拦截
 */

@Component
public class RefreshTokenInterceptor implements HandlerInterceptor {

  @Autowired
  StringRedisTemplate stringRedisTemplate;

  @Autowired
  JwtHelper jwtHelper;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    //前端请求时带上authorization
    String token = request.getHeader("authorization");
    if (StringUtils.isBlank(token)) {
      //未登录，直接放行，由登录拦截器拦截
      return true;
    }
        /*boolean expiration = jwtHelper.isExpiration(token);
        if (expiration){
            //token过期
            response.setHeader("登录拦截器：","凭证已过期，请重新登录");
            return false;
        }*/

    //存在authorization则校验是否过期
        /*
        if (UserHolder.getUser()==null){
            response.setStatus(402);
            return false;
            Long userId = UserHolder.getUser().getUserId();
        }*/
    //从redis获取token
    String tokenKey = Common.LOGIN_TOKEN_KEY + token;
    Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(tokenKey);
    if (map.isEmpty()) {
      //redis中存储的登录态已失效，放行，让登录拦截器拦截
      return true;
    }
    LoginUserVO loginUserVO = BeanUtil.fillBeanWithMap(map, new LoginUserVO(), false);
    //4.将用户信息保存到ThreadLocal中
    UserHolder.saveUser(loginUserVO);

       /* String redisToken = stringRedisTemplate.opsForValue().get(tokenKey);
        if (StringUtils.isBlank(redisToken)){
            //redis的token过期，需要重新登录
            return true;
        }*/

    //5.刷新redis的token有效期
    stringRedisTemplate.expire(tokenKey, Common.LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {
    //// 移除用户,防止内存泄漏!!!
    UserHolder.removeUser();
  }
}
