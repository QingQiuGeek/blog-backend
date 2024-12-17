package com.serein.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serein.model.request.CategoryRequest.CategoryPageRequest;
import com.serein.model.vo.CategoryVO.CategoryVO;
import com.serein.service.CategoryService;
import com.serein.util.BaseResponse;
import com.serein.util.ResultUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:懒大王Smile
 * @Date: 2024/12/8
 * @Time: 15:14
 * @Description:
 */

@RequestMapping("/category")
@RestController
public class CategoryController {

  @Autowired
  CategoryService categoryService;

  /**
   * @param categoryPageRequest
   * @return
   * @Description: 获取分类专栏页面的类别
   */
  @PostMapping("/getCategories")
  public BaseResponse<Page<List<CategoryVO>>> getCategories(
      @RequestBody CategoryPageRequest categoryPageRequest) {
    Page<List<CategoryVO>> categoryMap = categoryService.getCategories(categoryPageRequest);
    return ResultUtil.success(categoryMap);
  }

}
