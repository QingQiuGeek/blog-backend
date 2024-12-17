package com.serein;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 懒大王Smile
 */
@SpringBootApplication
@EnableAsync
// @EnableCaching//开启缓存
 @EnableTransactionManagement //开启注解方式的事务管理
// @EnableScheduling//开启定时任务增量同步
// 如果mapper层已经使用了@Mapper注解，那么这里就没有必要使用mapperScan了
// @MapperScan("com.serein.mapper")
public class BlogApplication {

  public static void main(String[] args) {
    SpringApplication.run(BlogApplication.class, args);
  }

}
