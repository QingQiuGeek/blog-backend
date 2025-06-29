package com.serein.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serein.annotation.RoleCheck;
import com.serein.constants.UserRole;
import com.serein.model.dto.categoryDTO.CategoryDTO;
import com.serein.model.entity.Category;
import com.serein.model.request.CategoryRequest.AdminCategoryPageRequest;
import com.serein.service.CategoryService;
import com.serein.util.BR;
import com.serein.util.R;
import jakarta.annotation.Resource;
import java.util.List;
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
 * @Description: 管理员 类别
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
//  @RoleCheck(mustRole = UserRole.ADMIN_ROLE)
  @PostMapping("/getCategories")
  public BR<Page<List<Category>>> getAdminCategories(
      @RequestBody AdminCategoryPageRequest adminCategoryPageRequest) {
    return R.ok(categoryService.getAdminCategories(adminCategoryPageRequest));
  }

  /**
   * @param addCategoryDTO
   * @return
   * @description 返回类别id
   */
//  @RoleCheck(mustRole = UserRole.ADMIN_ROLE)
  @PostMapping("/addCategory")
  public BR<Long> addCategory(@RequestBody CategoryDTO addCategoryDTO) {
    return R.ok(categoryService.addCategory(addCategoryDTO));
  }

  /**
   * @param updateCategoryDTO
   * @return
   * @description 更新类别
   */
//  @RoleCheck(mustRole = UserRole.ADMIN_ROLE)
  @PostMapping("/updateCategory")
  public BR<Boolean> updateCategory(@RequestBody CategoryDTO updateCategoryDTO) {
    return R.ok(categoryService.updateCategory(updateCategoryDTO));
  }


  /**
   * @param categoryId
   * @return
   * @description 根据id删除类别
   */
//  @RoleCheck(mustRole = UserRole.ADMIN_ROLE)
  @PutMapping("/delete/{categoryId}")
  public BR<Boolean> deleteCategory(@PathVariable Long categoryId) {
    return R.ok(categoryService.deleteCategory(categoryId));
  }


}
