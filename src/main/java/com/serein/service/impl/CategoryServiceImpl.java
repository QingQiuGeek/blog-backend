package com.serein.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.domain.entity.Category;
import com.serein.service.CategoryService;
import com.serein.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

/**
* @author 懒大王Smile
* @description 针对表【category(类别表)】的数据库操作Service实现
* @createDate 2024-09-12 22:19:13
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

}




