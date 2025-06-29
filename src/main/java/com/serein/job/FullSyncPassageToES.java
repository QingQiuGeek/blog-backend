package com.serein.job;


import cn.hutool.core.collection.CollUtil;
import com.serein.mapper.PassageMapper;
import com.serein.model.dto.passageDTO.PassageESDTO;
import com.serein.model.entity.Passage;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

/**
 * @Author:懒大王Smile
 * @Date: 2024/10/24
 * @Time: 22:27
 * @Description: 全量同步文章到ES
 * <p>
 * CommandLineRunner 接口的类会在应用启动时自动执行。具体机制如下： 如果@Component取消注释，则将 FullSyncPassageToES 类注册为 Spring
 * 上下文中的一个 bean。 Spring 在启动时会扫描所有带有 @Component 注解的类，并将它们实例化并纳入应用上下文。 CommandLineRunner 接口：当 Spring
 * Boot 应用启动后，会自动调用所有实现了 CommandLineRunner 接口的类的 run 方法。 因此，FullSyncPassageToES 中的 run 方法会在应用启动时执行。
 */

//取消注释开启任务
//@Component
@Slf4j
public class FullSyncPassageToES implements CommandLineRunner {

  @Resource
  private SyncDataToES syncDataToES;

  @Resource
  private PassageMapper passageMapper;

  @Override
  public void run(String... args) {
    List<Passage> postList = passageMapper.selectPassageESData();
    if (CollUtil.isEmpty(postList)) {
      return;
    }
    List<PassageESDTO> passageESDTOList = syncDataToES.objToESDto(postList);
    final int pageSize = 5;
    int total = passageESDTOList.size();
    log.info("FullSyncPassageTOES start, total {}", total);
    syncDataToES.syncDataToES(total, pageSize, passageESDTOList);
    log.info("FullSyncPassageTOES end, total {}", total);
  }

}
