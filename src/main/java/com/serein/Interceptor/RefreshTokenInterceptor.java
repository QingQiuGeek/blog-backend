package com.serein.Interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.serein.constants.Common;
import com.serein.domain.UserHolder;
import com.serein.domain.dto.LoginUserDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("authorization");
        if(StringUtils.isBlank(token)){
          //未登录，直接放行，由登录拦截器拦截
          return true;
        }

        //2.获取用户信息
        String tokenKey=Common.LOGIN_TOKEN_KEY+token;
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(tokenKey);
        if (map.isEmpty()){
            //redis中存储的登录态已失效，放行
            return true;
        }
        //3.将用户信息转换为LoginUserDTO
        LoginUserDTO userDTO = BeanUtil.fillBeanWithMap(map, new LoginUserDTO(), false);
        //4.将用户信息保存到ThreadLocal中
        UserHolder.saveUser(userDTO);
        //5.更新缓存时间
        stringRedisTemplate.expire(tokenKey,Common.LOGIN_TOKEN_TTL, TimeUnit.MINUTES);

        // Object userDto = request.getSession().getAttribute(Common.USER_LOGIN_STATE);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        //// 移除用户,防止内存泄漏!!!
        UserHolder.removeUser();
    }
}
