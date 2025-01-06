package com.serein.config;

import com.serein.exception.ExecutionRejectHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @Author:懒大王Smile
 * @Date: 2024/12/30
 * @Time: 23:29
 * @Description:
 */

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

  @Bean
  public ThreadPoolTaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    //线程数量>core放入queue，queue满了，再来的任务会创建新线程，直到线程数量=max，之后的任务就会被拒绝
    executor.setMaxPoolSize(20);
//    缓存队列（阻塞队列）当核心线程数达到最大时，新任务会放在队列中排队等待执行
    executor.setQueueCapacity(25);
    executor.setThreadNamePrefix("async-sendCode");
    //自定义拒绝策略
    executor.setRejectedExecutionHandler(new ExecutionRejectHandler());
    executor.initialize();
    log.info("线程池初始化成功，核心线程数：" + executor.getCorePoolSize() +
        ", 最大线程数：" + executor.getMaxPoolSize() +
        ", 队列容量：" + executor.getQueueCapacity());
    return executor;
  }

}
