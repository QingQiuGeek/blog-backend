package com.serein.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.exception.BusinessException;
import com.serein.mapper.TagsMapper;
import com.serein.model.dto.tagDTO.TagDTO;
import com.serein.model.entity.Tags;
import com.serein.model.request.TagRequest.AdminTagPageRequest;
import com.serein.model.vo.tagVO.TagVO;
import com.serein.service.TagsService;
import com.serein.util.IPUtil;
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
 * @description 针对表【tags(标签表)】的数据库操作Service实现
 * @createDate 2024-12-03 18:38:58
 */
@Service
@Slf4j
public class TagsServiceImpl extends ServiceImpl<TagsMapper, Tags>
    implements TagsService {

  @Autowired
  TagsMapper tagsMapper;

  @Override
  public List<TagVO> getRandomTags() {
    IPUtil.isHotIp();
    List<Tags> tagList = tagsMapper.getRandomTags();
    log.info("getRandomTags tagList：{}", tagList);
    List<TagVO> tagVOList = getTagVOList(tagList);
    return tagVOList;
  }

  @Override
  public Long addTag(TagDTO addTagDTO) {
    Tags tags = new Tags();
    BeanUtils.copyProperties(addTagDTO, tags);
    tags.setUpdateTime(new Date(addTagDTO.getUpdateTime()));
    tags.setCreateTime(new Date(addTagDTO.getCreateTime()));
    tagsMapper.insertTag(tags);
    Long tagId = tags.getTagId();
    if (tagId == null) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.ADD_ERROR);
    }
    log.info("insert a new tag:{} ,tagId={}", tags, tagId);
    return tagId;
  }

  @Override
  public boolean updateTag(TagDTO updateTagDTO) {
    Tags tag = new Tags();
    BeanUtils.copyProperties(updateTagDTO, tag);
    Long updateTime = updateTagDTO.getUpdateTime();
    tag.setUpdateTime(new Date(updateTime));
    boolean b = tagsMapper.updateTag(tag);
    if (b) {
      log.info("update tag success ：{}", tag);
      return true;
    }
    throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);

  }

  @Override
  public Page<List<Tags>> getAdminTags(AdminTagPageRequest adminTagPageRequest) {
    int currentPage = adminTagPageRequest.getCurrentPage();
    int pageSize = adminTagPageRequest.getPageSize();
    String tagName = adminTagPageRequest.getTagName();
    Date startTime = adminTagPageRequest.getStartTime();
    Date endTime = adminTagPageRequest.getEndTime();
    Long tagId = adminTagPageRequest.getTagId();
    Long categoryId = adminTagPageRequest.getCategoryId();

    Page<Tags> tagPage = new Page<>(currentPage, pageSize);
    Page<Tags> page = null;
    try {
      page = page(tagPage,
          new LambdaQueryWrapper<Tags>().orderByDesc(Tags::getUpdateTime)
              .eq(tagId != null, Tags::getTagId, tagId)
              .eq(categoryId != null, Tags::getCategoryId, categoryId)
              .like(StringUtils.isNotBlank(tagName), Tags::getTagName, tagName)
              .lt(endTime != null, Tags::getCreateTime, endTime)
              .gt(startTime != null, Tags::getCreateTime, startTime)
      );
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.DB_FAIL);
    }
    List<Tags> records = page.getRecords();
    long total = page.getTotal();
    log.info("Tag records on page【{}】，total【{}】：{}", page, total, records);
    Page<List<Tags>> adminTagVOPage = new Page<>(currentPage, pageSize);
    adminTagVOPage.setTotal(total);
    adminTagVOPage.setRecords(Collections.singletonList(records));
    return adminTagVOPage;
  }

  //TODO 事务
  @Override
  public boolean deleteTag(Long tagId) {
    int num1 = tagsMapper.deleteById(tagId);
    if (num1 == 0) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.DELETE_ERROR);
    }
    log.info("delete tag success,tagId：{},deleteTagNum：{}",
        tagId, num1);
    return true;
  }

  private List<TagVO> getTagVOList(List<Tags> tagList) {
    ArrayList<TagVO> tagVOS = new ArrayList<>();
    tagList.forEach(tag -> {
      TagVO tagVO = new TagVO();
      BeanUtils.copyProperties(tag, tagVO);
      tagVOS.add(tagVO);
    });
    return tagVOS;
  }
}




