package com.serein.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/19
 * @Time: 8:01
 * @Description: cors配置 ，该配置类已经失效了，因为有拦截器
 */

//@Configuration
public class CorsConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    // 覆盖所有请求
    registry.addMapping("/**")
        //允许所有来源
        .allowedOrigins("*")
        // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
        .allowedOriginPatterns("*")
        // 允许发送 Cookie
        .allowCredentials(true)
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .exposedHeaders("*")
        .maxAge(3600);
  }
}
