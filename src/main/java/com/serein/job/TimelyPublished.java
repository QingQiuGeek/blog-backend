package com.serein.job;

import cn.hutool.core.collection.CollUtil;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.serein.mapper.PassageMapper;
import com.serein.mapper.PassageTimePublishMapper;
import com.serein.model.entity.PassageTimePublish;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author:懒大王Smile
 * @Date: 2024/12/21
 * @Time: 18:37
 * @Description: 定时扫描passage把定时发布的文章改为发布
 */

//每三分钟执行一次同步近四分钟数据，容错1min
@Slf4j
@Component
public class TimelyPublished {

  //每三分钟执行一次
  private final int SCAN_MINUTES = 3;

  @Resource
  private PassageMapper passageMapper;

  @Resource
  private PassageTimePublishMapper passageTimePublishMapper;

  public static final int retryNum = 3;

  public static final int retryWaitTime = 3;
  // 创建一个retryer实例，最多重试3次，每次等待2秒
  Retryer<Void> retryer = RetryerBuilder.<Void>newBuilder()
      .retryIfExceptionOfType(Exception.class)
      // 如果是Exception类型异常则进行重试
      .withWaitStrategy(WaitStrategies.fixedWait(retryWaitTime, TimeUnit.SECONDS))
      // 每次等待2秒
      .withStopStrategy(StopStrategies.stopAfterAttempt(retryNum))
      // 最多重试3次
      .build();

  @Scheduled(fixedRate = SCAN_MINUTES * 60 * 1000)
  public void run() {
    try {
      // 查询近 4 分钟内的数据
      Date minutesAgoDate = new Date(new Date().getTime() - (SCAN_MINUTES + 1) * 60 * 1000L);
      List<PassageTimePublish> passageTimePublishes = passageTimePublishMapper.scanPublishPassage(
          minutesAgoDate);

      if (CollUtil.isEmpty(passageTimePublishes)) {
        log.info("No find passage need publish in {} minutes", SCAN_MINUTES * 60 * 1000L);
        return;
      }

      // 处理每个 passageTimePublish
      passageTimePublishes.forEach(passageTimePublish -> {
        try {
          retryer.call(() -> {
            try {
              Long passageId = passageTimePublish.getPassageId();
              log.info("start publish passage, passageId: {}", passageId);
              int published = passageMapper.publishPassage(passageId);
              if (published != 1) {
                log.error("publish failed, passageId: {}", passageId);
              }
              return null;
            } catch (Exception e) {
              log.error("publish exception: {}", e);
              return null; // 如果发生异常，返回null
            }
          });
        } catch (Exception e) {
          log.error("Retry execution failed for passageId: {}, error: {}",
              passageTimePublish.getPassageId(), e.getMessage());
        }
      });
    } catch (Exception e) {
      log.error("Unexpected exception in run method: {}", e.getMessage(), e);
    }
  }


}
