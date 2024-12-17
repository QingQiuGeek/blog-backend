package com.serein.job;

import cn.hutool.core.collection.CollUtil;
import com.serein.esdao.PassageESDao;
import com.serein.model.dto.PassageDTO.PassageESDTO;
import com.serein.model.entity.Passage;
import com.serein.model.vo.PassageVO.PassageVO;
import com.serein.service.PassageService;
import com.serein.service.impl.PassageServiceImpl;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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

// todo 取消注释开启任务
//@Component
@Slf4j
public class FullSyncPassageToES implements CommandLineRunner {

  @Resource
  private PassageService passageService;

  @Resource
  private PassageESDao passageESDao;
  @Resource
  private PassageServiceImpl passageServiceImpl;

  @Override
  public void run(String... args) {
    List<Passage> postList = passageService.list();
    if (CollUtil.isEmpty(postList)) {
      return;
    }
    List<PassageESDTO> passageESDTOList = postList.stream()
        .map(passage -> {
          PassageVO passageVO = new PassageVO();
          BeanUtils.copyProperties(passage, passageVO);
          String tagsId = passage.getTagsId();
          if (StringUtils.isNotBlank(tagsId)) {
            Map<Long, String> tagMap = passageServiceImpl.getTagStrList(tagsId);
            passageVO.setPTagsMap(tagMap);
            return PassageESDTO.objToDto(passageVO);
          }
          return PassageESDTO.objToDto(passageVO);
        })
        .collect(Collectors.toList());
    final int pageSize = 500;
    int total = passageESDTOList.size();
    log.info("FullSyncPassageTOES start, total {}", total);
    for (int i = 0; i < total; i += pageSize) {
      int end = Math.min(i + pageSize, total);
      log.info("sync from {} to {}", i, end);
      passageESDao.saveAll(passageESDTOList.subList(i, end));
    }
    log.info("FullSyncPassageTOES end, total {}", total);
  }
}
