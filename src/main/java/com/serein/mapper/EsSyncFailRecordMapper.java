package com.serein.mapper;

import com.serein.model.entity.EsSyncFailRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 懒大王Smile
* @description 针对表【es_sync_fail_record(mysql同步数据到ES失败记录表)】的数据库操作Mapper
* @createDate 2024-12-20 13:52:09
* @Entity com.serein.model.entity.EsSyncFailRecord
*/
@Mapper
public interface EsSyncFailRecordMapper extends BaseMapper<EsSyncFailRecord> {

}




