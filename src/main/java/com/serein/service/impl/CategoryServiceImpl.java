package com.serein.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.mapper.CategoryMapper;
import com.serein.model.CategoryPageRequest;
import com.serein.model.entity.Category;
import com.serein.model.vo.CategoryVO.CategoryVO;
import com.serein.service.CategoryService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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

  @Override
  public Page<List<CategoryVO>> getCategories(CategoryPageRequest categoryPageRequest) {
    int currentPage = categoryPageRequest.getCurrentPage();
    int pageSize = categoryPageRequest.getPageSize();
    Page<Category> categoryPage = new Page<>(currentPage,pageSize);
    Page<Category> page = page(categoryPage,
        new LambdaQueryWrapper<Category>().orderByDesc(Category::getUpdateTime));
    List<Category> records = page.getRecords();
    long total = page.getTotal();
    log.info("Category records on page【{}】，total【{}】：{}",page,total,records);
    List<CategoryVO> categoryVOList = getCategoryVOList(records);
    Page<List<CategoryVO>> categoryVOPage = new Page<>(currentPage, pageSize);
    categoryVOPage.setRecords(Collections.singletonList(categoryVOList));
    categoryVOPage.setTotal(total);
    return categoryVOPage;
  }

  private List<CategoryVO> getCategoryVOList(List<Category> categoryList){
    ArrayList<CategoryVO> categoryVOS = new ArrayList<>();
    categoryList.forEach(category -> {
      CategoryVO categoryVO = new CategoryVO();
      BeanUtils.copyProperties(category,categoryVO);
      categoryVOS.add(categoryVO);
    });
    return categoryVOS;
  }
}




