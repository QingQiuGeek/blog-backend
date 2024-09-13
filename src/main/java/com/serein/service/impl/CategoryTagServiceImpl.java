package com.serein.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.domain.entity.CategoryTag;
import com.serein.service.CategoryTagService;
import com.serein.mapper.CategoryTagMapper;
import org.springframework.stereotype.Service;

/**
* @author 懒大王Smile
* @description 针对表【category_tag(类别-标签表)】的数据库操作Service实现
* @createDate 2024-09-12 22:19:13
*/
@Service
public class CategoryTagServiceImpl extends ServiceImpl<CategoryTagMapper, CategoryTag>
    implements CategoryTagService{

}




