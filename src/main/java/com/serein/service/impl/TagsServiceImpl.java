package com.serein.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.constants.Common;
import com.serein.mapper.TagsMapper;
import com.serein.model.entity.Tags;
import com.serein.model.vo.TagVO.TagVO;
import com.serein.service.TagsService;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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
    List<Tags> tagList = tagsMapper.getRandomTags();
    log.info("getRandomTags tagList：{}",tagList);
    List<TagVO> tagVOList = getTagVOList(tagList);
    return tagVOList;
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




