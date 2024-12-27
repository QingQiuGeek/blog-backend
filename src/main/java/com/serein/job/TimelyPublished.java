package com.serein.job;

import com.serein.constants.Common;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.exception.BusinessException;
import com.serein.mapper.PassageMapper;
import com.serein.mapper.PassageTimePublishMapper;
import com.serein.service.impl.PassageServiceImpl;
import java.util.Date;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @Author:懒大王Smile
 * @Date: 2024/12/21
 * @Time: 18:37
 * @Description: 基于redisson的延迟队列实现文章定时发布
 */

//加上@Component Redisson延迟队列启动
@Component
@Slf4j
public class TimelyPublished implements CommandLineRunner {

  @Resource
  private PassageMapper passageMapper;

  @Resource
  private PassageServiceImpl passageServiceImpl;

  @Override
  public void run(String... args) throws Exception {
    new Thread(() -> {
      while (true) {
        try {
          Long passageId = passageServiceImpl.getDelayQueue(Common.TIME_PUBLISH_KEY);
          if (passageId != null) {
            int i = passageMapper.publishPassage(passageId);
            if (i != 1) {
              throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.OPERATION_ERROR);
            }
            log.info("定时发布文章成功，文章ID：{}",passageId);
          }
        } catch (InterruptedException e) {
          log.error("Redisson延迟队列异常中断 {}", e.getMessage());
        }
      }
    }).start();
    log.info("Redisson延迟队列启动成功");
  }
}
