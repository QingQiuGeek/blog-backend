package com.serein.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
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

  @Bean
  public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    return redisTemplate;
  }

  @Value("${spring.redis.host}")
  private String host;

  @Value("${spring.redis.port}")
  private String port;

  @Value("${spring.redis.username}")
  private String username;

  @Value("${spring.redis.password}")
  private String password;

  @Value("${spring.redis.database}")
  private int database;


  @Bean
  public RedissonClient redissonConfig() {
    Config config = new Config();
    // 使用单机模式连接 Redis
    config.useSingleServer().setAddress("redis://" + host + ":" + port).setUsername(username)
        .setPassword(password).setDatabase(database);
    try {
      return Redisson.create(config);
    } catch (RedisConnectionException e) {
      log.error("Failed to connect to Redis at {}:{}", host, port, e);
      throw e;
    }
  }


}
