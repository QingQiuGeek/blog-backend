package com.serein.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.serein.mapper.TagsMapper;
import com.serein.model.entity.Tags;
import com.serein.service.TagsService;
import org.springframework.stereotype.Service;

/**
* @author 懒大王Smile
* @description 针对表【tags(标签表)】的数据库操作Service实现
* @createDate 2024-12-03 18:38:58
*/
@Service
public class TagsServiceImpl extends ServiceImpl<TagsMapper, Tags>
    implements TagsService {

}




