package com.serein.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serein.model.dto.categoryDTO.CategoryDTO;
import com.serein.model.entity.Category;
import com.serein.model.request.CategoryRequest.AdminCategoryPageRequest;
import com.serein.service.CategoryService;
import com.serein.util.BaseResponse;
import com.serein.util.ResultUtil;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:懒大王Smile
 * @Date: 2024/12/8
 * @Time: 15:14
 * @Description:
 */

@RequestMapping("/admin/category")
@RestController
public class AdminCategoryController {

  @Resource
  private CategoryService categoryService;


  /**
   * 获取管理页的类别列表
   *
   * @param adminCategoryPageRequest
   * @return
   */
  @PostMapping("/getCategories")
  public BaseResponse<Page<List<Category>>> getAdminCategories(
      @RequestBody AdminCategoryPageRequest adminCategoryPageRequest) {
    Page<List<Category>> categoryMap = categoryService.getAdminCategories(adminCategoryPageRequest);
    return ResultUtil.success(categoryMap);
  }

  /**
   * @param addCategoryDTO
   * @return
   * @description 返回类别id
   */
  @PostMapping("/addCategory")
  public BaseResponse<Long> addCategory(@RequestBody CategoryDTO addCategoryDTO) {
    Long categoryId = categoryService.addCategory(addCategoryDTO);
    return ResultUtil.success(categoryId);
  }

  /**
   * @param updateCategoryDTO
   * @return
   * @description 更新类别
   */
  @PostMapping("/updateCategory")
  public BaseResponse<Boolean> updateCategory(@RequestBody CategoryDTO updateCategoryDTO) {
    boolean b = categoryService.updateCategory(updateCategoryDTO);
    return ResultUtil.success(b);
  }


  /**
   * @param categoryId
   * @return
   * @description 根据id删除类别
   */
  @PutMapping("/delete/{categoryId}")
  public BaseResponse<Boolean> deleteCategory(@PathVariable Long categoryId) {
    boolean b = categoryService.deleteCategory(categoryId);
    return ResultUtil.success(b);
  }


}
