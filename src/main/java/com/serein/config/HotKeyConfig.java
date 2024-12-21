package com.serein.config;

import com.jd.platform.hotkey.client.ClientStarter;
import javax.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author:懒大王Smile
 * @Date: 2024/12/20
 * @Time: 18:28
 * @Description:
 */

//@Configuration
//@ConfigurationProperties(prefix = "hotkey")
@Data
public class HotKeyConfig {

  /**
   * Etcd 服务器完整地址
   */
  private String etcdServer;

  /**
   * 应用名称
   */
  private String appName;

  /**
   * 批量推送 key 的间隔时间
   */
  private long pushPeriod;

  private int caffeineSize;

  /**
   * 初始化 hotkey
   */
//  @Bean
  public void initHotkey() {
    ClientStarter.Builder builder = new ClientStarter.Builder();
    ClientStarter starter = builder.setAppName(appName)
        .setPushPeriod(pushPeriod)
        .setEtcdServer(etcdServer)
        .setCaffeineSize(caffeineSize)
        .build();
    starter.startPipeline();
  }

}
