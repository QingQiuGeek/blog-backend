package com.serein.job;

import cn.hutool.core.collection.CollUtil;
import com.serein.mapper.PassageMapper;
import com.serein.model.dto.passageDTO.PassageESDTO;
import com.serein.model.entity.Passage;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author:懒大王Smile
 * @Date: 2024/10/24
 * @Time: 22:27
 * @Description: 增量同步文章到ES
 */
//@Component
@Slf4j
public class IncSyncPassageToES {

  @Resource
  private PassageMapper passageMapper;

  @Resource
  private SyncDataToES syncDataToES;

  //单位分钟，同步多久前的文章
  private  final int AGO_MINUTES = 4;

  //单位分钟，每三分钟同步一次三分钟之前的数据
  private  final int RATE_MINUTES = 3;

  //开启定时任务
  @Scheduled(fixedRate = RATE_MINUTES * 60 * 1000)
  public void run() {
    // 查询近 5 分钟内的数据
    Date minutesAgoDate = new Date(new Date().getTime() - AGO_MINUTES * 60 * 1000L);
    List<Passage> passageList = passageMapper.asyncPassageToEs(minutesAgoDate);

    if (CollUtil.isEmpty(passageList)) {
      log.info("No find new add passage in {} minutes", AGO_MINUTES);
      return;
    }

    List<PassageESDTO> passageESDTOList = syncDataToES.objToESDto(passageList);

    //每个批次处理的项目数
    final int pageSize = 50;
    int total = passageESDTOList.size();
    log.info("IncSyncPassageToES start, total {}", total);
    syncDataToES.syncDataToES(total, pageSize, passageESDTOList);

    log.info("IncSyncPassageTOES end, total {}", total);
  }
}
