package com.serein.job;


import cn.hutool.core.collection.CollUtil;
import com.serein.mapper.PassageMapper;
import com.serein.mapper.PassageTagMapper;
import com.serein.mapper.TagsMapper;
import com.serein.model.dto.PassageDTO.PassageESDTO;
import com.serein.model.entity.Passage;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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

// todo 取消注释开启任务
@Component
@Slf4j
public class FullSyncPassageToES implements CommandLineRunner {

  @Resource
  private SyncDataToES syncDataToES;

  @Resource
  PassageMapper passageMapper;

  @Override
  public void run(String... args) {
    List<Passage> postList = passageMapper.selectPassageESData();
    if (CollUtil.isEmpty(postList)) {
      return;
    }
    List<PassageESDTO> passageESDTOList = syncDataToES.objToESDto(postList);

    final int pageSize = 500;
    int total = passageESDTOList.size();
    log.info("FullSyncPassageTOES start, total {}", total);
    syncDataToES.syncDataToES(total, pageSize, passageESDTOList);
    log.info("FullSyncPassageTOES end, total {}", total);
  }

}
