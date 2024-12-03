package com.serein.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serein.model.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 懒大王Smile
 * @description 针对表【category(类别表)】的数据库操作Mapper
 * @createDate 2024-09-12 22:19:13
 * @Entity com.serein.domain.entity.Category
 */

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}




