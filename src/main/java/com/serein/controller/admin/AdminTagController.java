package com.serein.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serein.model.dto.tagDTO.TagDTO;
import com.serein.model.entity.Tags;
import com.serein.model.request.TagRequest.AdminTagPageRequest;
import com.serein.service.TagsService;
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
 * @Time: 16:33
 * @Description:
 */

@RestController
@RequestMapping("/admin/tag")
public class AdminTagController {

  @Resource
  private TagsService tagsService;

  /**
   * @param adminTagPageRequest
   * @return
   * @description 获取管理页的标签列表
   */
  @PostMapping("/getTags")
  public BaseResponse<Page<List<Tags>>> getAdminTags(
      @RequestBody AdminTagPageRequest adminTagPageRequest) {
    Page<List<Tags>> tagMap = tagsService.getAdminTags(adminTagPageRequest);
    return ResultUtil.success(tagMap);
  }

  /**
   * @param addTagDTO
   * @return
   * @description 返回标签id
   */
  @PostMapping("/addTag")
  public BaseResponse<Long> addTag(@RequestBody TagDTO addTagDTO) {
    Long tagId = tagsService.addTag(addTagDTO);
    return ResultUtil.success(tagId);
  }

  /**
   * @param updateTagDTO
   * @return
   * @description 更新标签
   */
  @PostMapping("/updateTag")
  public BaseResponse<Boolean> updateTag(@RequestBody TagDTO updateTagDTO) {
    boolean b = tagsService.updateTag(updateTagDTO);
    return ResultUtil.success(b);
  }


  /**
   * @param tagId
   * @return
   * @description 根据id删除标签
   */
  @PutMapping("/delete/{tagId}")
  public BaseResponse<Boolean> deleteTag(@PathVariable Long tagId) {
    boolean b = tagsService.deleteTag(tagId);
    return ResultUtil.success(b);
  }


}
