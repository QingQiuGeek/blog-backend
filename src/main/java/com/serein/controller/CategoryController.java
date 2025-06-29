package com.serein.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serein.model.request.CategoryRequest.CategoryPageRequest;
import com.serein.model.vo.categoryVO.CategoryAndTags;
import com.serein.model.vo.categoryVO.CategoryVO;
import com.serein.service.CategoryService;
import com.serein.util.BR;
import com.serein.util.R;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:懒大王Smile
 * @Date: 2024/12/8
 * @Time: 15:14
 * @Description: 类别Controller
 */

@RequestMapping("/category")
@RestController
public class CategoryController {

  @Resource
  private CategoryService categoryService;

  /**
   * @param categoryPageRequest
   * @return
   * @Description: 获取分类专栏页面的类别
   */
  @PostMapping("/getCategories")
  public BR<Page<List<CategoryVO>>> getCategories(
      @RequestBody CategoryPageRequest categoryPageRequest) {
    return R.ok(categoryService.getCategories(categoryPageRequest));
  }


  /**
   * 修改个人标签和文章标签
   * @return
   */
  @GetMapping("/getCategoriesAndTags")
  public BR<List<CategoryAndTags>> getCategoriesAndTags() {
    return R.ok(categoryService.getCategoriesAndTags());
  }

}
