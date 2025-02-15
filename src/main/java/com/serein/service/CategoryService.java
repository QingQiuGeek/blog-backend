package com.serein.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.serein.model.dto.categoryDTO.CategoryDTO;
import com.serein.model.entity.Category;
import com.serein.model.request.CategoryRequest.AdminCategoryPageRequest;
import com.serein.model.request.CategoryRequest.CategoryPageRequest;
import com.serein.model.vo.categoryVO.CategoryAndTags;
import com.serein.model.vo.categoryVO.CategoryVO;
import java.util.List;

/**
 * @author 懒大王Smile
 * @description 针对表【category(类别表)】的数据库操作Service
 * @createDate 2024-09-12 22:19:13
 */
public interface CategoryService extends IService<Category> {

  Page<List<CategoryVO>> getCategories(CategoryPageRequest categoryPageRequest);

  Page<List<Category>> getAdminCategories(AdminCategoryPageRequest adminCategoryPageRequest);

  Long addCategory(CategoryDTO categoryDTO);

  boolean updateCategory(CategoryDTO updateCategoryDTO);

  boolean deleteCategory(Long categoryId);

  List<CategoryAndTags> getCategoriesAndTags();
}
