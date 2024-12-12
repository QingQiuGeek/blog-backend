package com.serein.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.serein.model.AdminCategoryPageRequest;
import com.serein.model.AdminTagPageRequest;
import com.serein.model.CategoryPageRequest;
import com.serein.model.dto.CategoryDTO.CategoryDTO;
import com.serein.model.dto.TagDTO.TagDTO;
import com.serein.model.entity.Category;
import com.serein.model.entity.Tags;
import com.serein.model.vo.CategoryVO.CategoryVO;
import com.serein.model.vo.TagVO.TagVO;
import java.util.List;

/**
* @author 懒大王Smile
* @description 针对表【tags(标签表)】的数据库操作Service
* @createDate 2024-12-03 18:38:58
*/
public interface TagsService extends IService<Tags> {

  List<TagVO> getRandomTags();

  Long addTag(TagDTO addTagDTO);

  boolean updateTag(TagDTO updateTagDTO);

  Page<List<Tags>> getAdminTags(AdminTagPageRequest adminTagPageRequest);

  boolean deleteTag(Long tagId);
}
