package com.serein.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/18
 * @Time: 16:45
 * @Description: 设置redis序列化方式
 */

@Slf4j
@Configuration
public class RedisConfig {

  //todo 优化：redis序列化
  @Bean
  public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();

    redisTemplate.setConnectionFactory(redisConnectionFactory);
    return redisTemplate;
  }
}
