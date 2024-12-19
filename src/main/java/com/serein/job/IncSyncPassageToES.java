package com.serein.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.serein.esdao.PassageESDao;
import com.serein.mapper.PassageMapper;
import com.serein.mapper.PassageTagMapper;
import com.serein.mapper.TagsMapper;
import com.serein.model.dto.PassageDTO.PassageDTO;
import com.serein.model.dto.PassageDTO.PassageESDTO;
import com.serein.model.entity.Passage;
import com.serein.model.entity.PassageTag;
import com.serein.model.entity.Tags;
import com.serein.model.vo.PassageVO.PassageVO;
import com.serein.service.impl.PassageServiceImpl;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author:懒大王Smile
 * @Date: 2024/10/24
 * @Time: 22:27
 * @Description: 增量同步文章到ES
 */
@Component
@Slf4j
public class IncSyncPassageToES {

  @Resource
  private PassageMapper passageMapper;

  @Resource
  private PassageESDao passageESDao;

  @Resource
  PassageTagMapper passageTagMapper;

  @Resource
  TagsMapper tagsMapper;

  //单位分钟，同步多久前的文章
  private static final int AGO_MINUTES = 3;

  //单位分钟，每三分钟同步一次三分钟之前的数据
  private static final int RATE_MINUTES = 3;

  @Scheduled(fixedRate = RATE_MINUTES * 60 * 1000)
  public void run() {
    // 查询近 5 分钟内的数据
    Date minutesAgoDate = new Date(new Date().getTime() - AGO_MINUTES * 60 * 1000L);
    List<Passage> passageList = passageMapper.listPassageWithNODelete(minutesAgoDate);
    if (CollUtil.isEmpty(passageList)) {
      log.info("No find new add passage in {} minutes", AGO_MINUTES * 60 * 1000L);
      return;
    }
    List<PassageESDTO> passageESDTOList = passageList.stream()
        .map(passage -> {
          PassageESDTO passageESDTO = new PassageESDTO();
          BeanUtils.copyProperties(passage, passageESDTO);
          List<PassageTag> passageTags = passageTagMapper.selectTagIdByPassageId(
              passage.getPassageId());
          List<Long> tagIdList = passageTags.stream().map(PassageTag::getTagId)
              .collect(Collectors.toList());
          List<Tags> tags = tagsMapper.selectBatchIds(tagIdList);
          List<String> tagNameList = tags.stream().map(Tags::getTagName)
              .collect(Collectors.toList());
          String jsonStr = JSONUtil.toJsonStr(tagNameList);
          passageESDTO.setTagStr(jsonStr);
          return passageESDTO;
        })
        .collect(Collectors.toList());
    //每个批次处理的项目数
    final int pageSize = 50;
    int total = passageESDTOList.size();
    log.info("IncSyncPassageToES start, total {}", total);
    for (int i = 0; i < total; i += pageSize) {
      //计算当前批次的结束索引，确保不会超过总数。
      int end = Math.min(i + pageSize, total);
      log.info("sync from index {} to {}", i, end);
      //subList获取需要处理的列表部分。
      passageESDao.saveAll(passageESDTOList.subList(i, end));
    }
    log.info("IncSyncPassageTOES end, total {}", total);
  }
}
