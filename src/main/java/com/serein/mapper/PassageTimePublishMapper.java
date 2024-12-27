package com.serein.mapper;

import com.serein.model.entity.PassageTimePublish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Delete;

/**
* @author 懒大王Smile
* @description 针对表【passage_time_publish(文章定时发布表)】的数据库操作Mapper
* @createDate 2024-12-21 18:45:24
* @Entity com.serein.model.entity.PassageTimePublish
*/
public interface PassageTimePublishMapper extends BaseMapper<PassageTimePublish> {

  List<PassageTimePublish> scanPublishPassage(Date minutesAgoDate);

  @Delete("delete from blog.passage_time_publish where passageId=#{passageId}")
  void deleteByPassageId(Long passageId);
}




