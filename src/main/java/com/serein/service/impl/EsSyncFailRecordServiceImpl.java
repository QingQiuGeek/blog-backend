package com.serein.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.model.entity.EsSyncFailRecord;
import com.serein.service.EsSyncFailRecordService;
import com.serein.mapper.EsSyncFailRecordMapper;
import org.springframework.stereotype.Service;

/**
* @author 懒大王Smile
* @description 针对表【es_sync_fail_record(mysql同步数据到ES失败记录表)】的数据库操作Service实现
* @createDate 2024-12-20 13:52:09
*/
@Service
public class EsSyncFailRecordServiceImpl extends ServiceImpl<EsSyncFailRecordMapper, EsSyncFailRecord>
    implements EsSyncFailRecordService{

}




