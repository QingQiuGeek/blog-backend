package com.serein.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serein.model.entity.Category;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author 懒大王Smile
 * @description 针对表【category(类别表)】的数据库操作Mapper
 * @createDate 2024-09-12 22:19:13
 * @Entity com.serein.domain.entity.Category
 */

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

  void insertCategory(Category category);

  boolean updateCategory(Category category);

  @Select("select categoryId,categoryName from blog.category")
  List<Category> getAllCategories();
}




