package com.serein.job;

import cn.hutool.json.JSONUtil;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
//import com.serein.esdao.PassageESDao;
import com.serein.mapper.EsSyncFailRecordMapper;
import com.serein.mapper.PassageTagMapper;
import com.serein.mapper.TagsMapper;
import com.serein.mapper.UserMapper;
import com.serein.model.dto.passageDTO.PassageESDTO;
import com.serein.model.entity.EsSyncFailRecord;
import com.serein.model.entity.Passage;
import com.serein.model.entity.PassageTag;
import com.serein.model.entity.Tags;
import com.serein.model.entity.User;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @Author:懒大王Smile
 * @Date: 2024/12/20
 * @Time: 15:10
 * @Description:
 */

@Slf4j
//@Component
public class SyncDataToES {

  @Resource
  EsSyncFailRecordMapper esSyncFailRecordMapper;

  @Resource
//  private PassageESDao passageESDao;

  public final int retryNum = 3;

  public final int retryWaitTime = 3;

  // 创建一个retryer实例，最多重试3次，每次等待2秒
  Retryer<Void> retryer = RetryerBuilder.<Void>newBuilder()
      .retryIfExceptionOfType(Exception.class)
      // 如果是Exception类型异常则进行重试
      .withWaitStrategy(WaitStrategies.fixedWait(retryWaitTime, TimeUnit.SECONDS))
      // 每次等待2秒
      .withStopStrategy(StopStrategies.stopAfterAttempt(retryNum))
      // 最多重试3次
      .build();

  public void syncDataToES(int total, int pageSize, List<PassageESDTO> passageESDTOList) {
    for (int i = 1; i <= total; i += pageSize) {
      int end = Math.min(i + pageSize, total);
      log.info("sync from {} to {}", i, end);
      try {
        int finalI = i;
        retryer.call(() -> {
          try {
            List<PassageESDTO> passageESDTOS = passageESDTOList.subList(finalI, end);
            passageESDTOS.forEach(passageESDTO -> {
              log.info("同步文章:{}", passageESDTO.getPassageId());
            });
//            passageESDao.saveAll(passageESDTOS);
            log.info("该批次同步文章成功");
            return null;
          } catch (Exception e) {
            log.error("同步失败，进行重试", e);
            throw e;
          }
        });
      } catch (Exception e) {
        log.error("{}次同步失败，记录到fail_record表:{}", retryNum, e);
        Long passageId = passageESDTOList.get(i).getPassageId();
        EsSyncFailRecord esSyncFailRecord = new EsSyncFailRecord();
        esSyncFailRecord.setPassageId(passageId);
        int insert = esSyncFailRecordMapper.insert(esSyncFailRecord);
        if (insert != 1) {
          log.error("同步失败数据记录到表失败");
        }
      }
    }
  }


  @Resource
  UserMapper userMapper;

  @Resource
  PassageTagMapper passageTagMapper;

  @Resource
  TagsMapper tagsMapper;

  public List<PassageESDTO> objToESDto(List<Passage> passageList) {
    List<PassageESDTO> passageESDTOList = passageList.stream()
        .map(passage -> {
          PassageESDTO passageESDTO = new PassageESDTO();
          BeanUtils.copyProperties(passage, passageESDTO);
          List<PassageTag> passageTags = passageTagMapper.selectTagIdByPassageId(
              passage.getPassageId());
          List<Long> tagIdList = passageTags.stream().map(PassageTag::getTagId)
              .collect(Collectors.toList());
          if (!tagIdList.isEmpty()) {
            List<Tags> tags = tagsMapper.selectBatchIds(tagIdList);
            List<String> tagNameList = tags.stream().map(Tags::getTagName)
                .collect(Collectors.toList());
            String jsonStr = JSONUtil.toJsonStr(tagNameList);
            passageESDTO.setTagStr(jsonStr);
          }
          User authorInfo = userMapper.getAuthorInfo(passage.getAuthorId());
          passageESDTO.setAuthorName(authorInfo.getUserName());
          return passageESDTO;
        })
        .collect(Collectors.toList());
    return passageESDTOList;
  }

}
