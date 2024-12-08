package com.serein.config;

import com.serein.interceptor.LoginInterceptor;
import com.serein.interceptor.RefreshTokenInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/18
 * @Time: 16:48
 * @Description:
 */
@Component
@Slf4j
public class InterceptorsConfig extends WebMvcConfigurationSupport {

  @Autowired
  LoginInterceptor loginInterceptor;

  @Autowired
  RefreshTokenInterceptor refreshTokenInterceptor;

  @Override
  protected void addInterceptors(InterceptorRegistry registry) {

    log.info("注册自定义拦截器");
    registry.addInterceptor(refreshTokenInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns(
            "/doc.html/**",
            "/swagger-resources/**",
            "/webjars/**"
        ).order(0);
//        order越小，优先级越高

    registry.addInterceptor(loginInterceptor)
        .addPathPatterns()
        .excludePathPatterns(
            "/user/login",
            "/user/register",
            "/user/getUserInfo/{uid}",
            "/user/sendRegisterCode",
            "/user/find/{userName}",
            "/user/userInfoData",
            "/passage/search/uid/{uid}",
            "/passage/topCollects",
            "/passage/content/{uid}/{pid}",
            "/passage/homePassageList",
            "/passage/search/text",
            "/passage/passageInfo/{pid}",
            "/comment/{authorId}/{passageId}",
            "/category/getCategories",
            "/tag/getRandomTags",
            "/webjars/**",
            "/doc.html/**",
            "/swagger-resources/**"
        );
  }


  //没有该配置将无法使用swagger API测试
  @Override
  protected void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/META-INF/resources/");
    registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }
}
