package com.serein.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.serein.domain.entity.Passage;
import com.serein.utils.ResultUtils;

/**
* @author 懒大王Smile
* @description 针对表【passage(文章表)】的数据库操作Service
* @createDate 2024-09-12 22:19:13
*/
public interface PassageService extends IService<Passage> {

    ResultUtils getNewPassageList();
}
