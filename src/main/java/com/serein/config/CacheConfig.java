package com.serein.config;

import java.time.Duration;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * @Author:懒大王Smile
 * @Date: 2025/1/24
 * @Time: 12:05
 * @Description: redis缓存配置类
 */

@Configuration
public class CacheConfig extends CachingConfigurerSupport {

  @Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
    RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofSeconds(600));
    return RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(redisCacheConfiguration)
        .build();
  }

}