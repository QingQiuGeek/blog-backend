package com.serein.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serein.domain.entity.CategoryTag;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 懒大王Smile
* @description 针对表【category_tag(类别-标签表)】的数据库操作Mapper
* @createDate 2024-09-12 22:19:13
* @Entity com.serein.domain.entity.CategoryTag
*/

@Mapper
public interface CategoryTagMapper extends BaseMapper<CategoryTag> {

}




