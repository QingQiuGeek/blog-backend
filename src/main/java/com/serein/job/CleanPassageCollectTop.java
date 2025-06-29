package com.serein.job;

import com.serein.constants.Common;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author:懒大王Smile
 * @Date: 2025/1/12
 * @Time: 11:54
 * @Description: 定时清理redis的collect top中非前十的元素,只留下top10
 */

@Component
@Slf4j
public class CleanPassageCollectTop {

  @Resource
  StringRedisTemplate stringRedisTemplate;

  private final int RATE_MINUTES = 1;

  @Scheduled(fixedRate = RATE_MINUTES*60*1000)
  public void run(){
    String key= Common.TOP_COLLECT_PASSAGE;
    Long removeRange = stringRedisTemplate.opsForZSet().removeRange(key, 10L, Integer.MAX_VALUE);
//    log.info("清理passage top >10的数量： {}",removeRange);
  }
}
