package com.serein.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.domain.entity.Tag;
import com.serein.service.TagService;
import com.serein.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author 懒大王Smile
* @description 针对表【tag(标签表)】的数据库操作Service实现
* @createDate 2024-09-12 22:19:13
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService {

}




