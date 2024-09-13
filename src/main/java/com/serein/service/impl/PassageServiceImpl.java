package com.serein.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.domain.entity.Passage;
import com.serein.service.PassageService;
import com.serein.mapper.PassageMapper;
import com.serein.utils.ResultUtils;
import org.springframework.stereotype.Service;

/**
* @author 懒大王Smile
* @description 针对表【passage(文章表)】的数据库操作Service实现
* @createDate 2024-09-12 22:19:13
*/
@Service
public class PassageServiceImpl extends ServiceImpl<PassageMapper, Passage>
    implements PassageService {

    @Override
    public ResultUtils getNewPassageList() {

        return ResultUtils.ok("获取最新文章列表成功");
    }
}




