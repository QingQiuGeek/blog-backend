package com.serein.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serein.annotation.RoleCheck;
import com.serein.constants.UserRole;
import com.serein.model.dto.tagDTO.TagDTO;
import com.serein.model.entity.Tags;
import com.serein.model.request.TagRequest.AdminTagPageRequest;
import com.serein.service.TagsService;
import com.serein.util.BR;
import com.serein.util.R;
import java.util.List;
import jakarta.annotation.Resource;
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
 * @Description: 管理员 标签
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
//  @RoleCheck(mustRole = UserRole.ADMIN_ROLE)
  @PostMapping("/getTags")
  public BR<Page<List<Tags>>> getAdminTags(
      @RequestBody AdminTagPageRequest adminTagPageRequest) {
    return R.ok(tagsService.getAdminTags(adminTagPageRequest));
  }

  /**
   * @param addTagDTO
   * @return
   * @description 返回标签id
   */
//  @RoleCheck(mustRole = UserRole.ADMIN_ROLE)
  @PostMapping("/addTag")
  public BR<Long> addTag(@RequestBody TagDTO addTagDTO) {
    return R.ok(tagsService.addTag(addTagDTO));
  }

  /**
   * @param updateTagDTO
   * @return
   * @description 更新标签
   */
//  @RoleCheck(mustRole = UserRole.ADMIN_ROLE)
  @PostMapping("/updateTag")
  public BR<Boolean> updateTag(@RequestBody TagDTO updateTagDTO) {
    return R.ok(tagsService.updateTag(updateTagDTO));
  }


  /**
   * @param tagId
   * @return
   * @description 根据id删除标签
   */
//  @RoleCheck(mustRole = UserRole.ADMIN_ROLE)
  @PutMapping("/delete/{tagId}")
  public BR<Boolean> deleteTag(@PathVariable Long tagId) {
    return R.ok(tagsService.deleteTag(tagId));
  }


}
