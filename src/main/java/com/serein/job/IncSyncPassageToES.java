package com.serein.job;

import cn.hutool.core.collection.CollUtil;
import com.serein.esdao.PassageESDao;
import com.serein.mapper.PassageMapper;
import com.serein.model.dto.passageDTO.PassageESDTO;
import com.serein.model.entity.Passage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    private PassageESDao passageESDao;

    //单位分钟
    private static  final int AGO_MINUTES =600;

    //单位分钟
    private static  final int RATE_MINUTES =5;


    @Scheduled(fixedRate = RATE_MINUTES * 60* 1000)
    public void run() {
        // 查询近 5 分钟内的数据
        Date minutesAgoDate = new Date(new Date().getTime() - AGO_MINUTES * 60 * 1000L);
        List<Passage> passageList = passageMapper.listPassageWithNODelete(minutesAgoDate);
        if (CollUtil.isEmpty(passageList)) {
            log.info("No find new add passage in {}minutes",AGO_MINUTES * 60 * 1000L);
            return;
        }
        List<PassageESDTO> passageESDTOList = passageList.stream()
                .map(PassageESDTO::objToDto)
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
