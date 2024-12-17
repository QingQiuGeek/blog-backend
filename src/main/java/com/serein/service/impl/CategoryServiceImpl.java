package com.serein.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.exception.BusinessException;
import com.serein.mapper.CategoryMapper;
import com.serein.mapper.TagsMapper;
import com.serein.model.dto.CategoryDTO.CategoryDTO;
import com.serein.model.entity.Category;
import com.serein.model.request.CategoryRequest.AdminCategoryPageRequest;
import com.serein.model.request.CategoryRequest.CategoryPageRequest;
import com.serein.model.vo.CategoryVO.CategoryVO;
import com.serein.service.CategoryService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 懒大王Smile
 * @description 针对表【category(类别表)】的数据库操作Service实现
 * @createDate 2024-09-12 22:19:13
 */
@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService {

  @Autowired
  CategoryMapper categoryMapper;

  @Autowired
  TagsMapper tagsMapper;

  @Override
  public Page<List<CategoryVO>> getCategories(CategoryPageRequest categoryPageRequest) {
    int currentPage = categoryPageRequest.getCurrentPage();
    int pageSize = categoryPageRequest.getPageSize();
    Page<Category> categoryPage = new Page<>(currentPage, pageSize);
    Page<Category> page = page(categoryPage,
        new LambdaQueryWrapper<Category>().orderByDesc(Category::getUpdateTime));
    List<Category> records = page.getRecords();
    long total = page.getTotal();
    log.info("Category records on page【{}】，total【{}】：{}", page, total, records);
    List<CategoryVO> categoryVOList = getCategoryVOList(records);
    Page<List<CategoryVO>> categoryVOPage = new Page<>(currentPage, pageSize);
    categoryVOPage.setRecords(Collections.singletonList(categoryVOList));
    categoryVOPage.setTotal(total);
    return categoryVOPage;
  }

  @Override
  public Page<List<Category>> getAdminCategories(
      AdminCategoryPageRequest adminCategoryPageRequest) {
    int currentPage = adminCategoryPageRequest.getCurrentPage();
    int pageSize = adminCategoryPageRequest.getPageSize();
    String categoryName = adminCategoryPageRequest.getCategoryName();
    Long categoryId = adminCategoryPageRequest.getCategoryId();
    Date endTime = adminCategoryPageRequest.getEndTime();
    Date startTime = adminCategoryPageRequest.getStartTime();

    Page<Category> categoryPage = new Page<>(currentPage, pageSize);
    Page<Category> page = page(categoryPage,
        new LambdaQueryWrapper<Category>().orderByDesc(Category::getUpdateTime)
            .eq(categoryId != null, Category::getCategoryId, categoryId)
            .like(StringUtils.isNotBlank(categoryName), Category::getCategoryName, categoryName)
            .lt(endTime != null, Category::getCreateTime, endTime)
            .gt(startTime != null, Category::getCreateTime, startTime)
    );
    List<Category> records = page.getRecords();
    long total = page.getTotal();
    log.info("Category records on page【{}】，total【{}】：{}", page, total, records);
    Page<List<Category>> adminCategoryVOPage = new Page<>(currentPage, pageSize);
    adminCategoryVOPage.setTotal(total);
    adminCategoryVOPage.setRecords(Collections.singletonList(records));
    return adminCategoryVOPage;
  }

  @Override
  public Long addCategory(CategoryDTO categoryDTO) {
    Category category = new Category();
    BeanUtils.copyProperties(categoryDTO, category);
    category.setUpdateTime(new Date(categoryDTO.getUpdateTime()));
    category.setCreateTime(new Date(categoryDTO.getCreateTime()));
    categoryMapper.insertCategory(category);
    Long categoryId = category.getCategoryId();
    if (categoryId != null) {
      log.info("insert a new category:{} ,categoryId={}", category, categoryId);
      return categoryId;
    }
    throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.ADD_ERROR);
  }

  @Override
  public boolean updateCategory(CategoryDTO updateCategoryDTO) {
    Category category = new Category();
    BeanUtils.copyProperties(updateCategoryDTO, category);
    Long updateTime = updateCategoryDTO.getUpdateTime();
    category.setUpdateTime(new Date(updateTime));
    boolean b = categoryMapper.updateCategory(category);
    if (b) {
      log.info("update category success ：{}", updateCategoryDTO);
      return true;
    }
    throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
  }

  //TODO 事务
  @Override
  public boolean deleteCategory(Long categoryId) {
    int num1 = categoryMapper.deleteById(categoryId);
    int num2 = tagsMapper.deleteByCategoryId(categoryId);
    if (num1 != 0) {
      log.info("delete category success,categoryId：{},deleteCategoryNum：{}，deleteTagNum：{}",
          categoryId, num1, num2);
      return true;
    }
    throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.DELETE_ERROR);
  }


  private List<CategoryVO> getCategoryVOList(List<Category> categoryList) {
    ArrayList<CategoryVO> categoryVOS = new ArrayList<>();
    categoryList.forEach(category -> {
      CategoryVO categoryVO = new CategoryVO();
      BeanUtils.copyProperties(category, categoryVO);
      categoryVOS.add(categoryVO);
    });
    return categoryVOS;
  }
}




