package com.serein.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.domain.entity.UserCollects;
import com.serein.service.UserCollectsService;
import com.serein.mapper.UserCollectsMapper;
import org.springframework.stereotype.Service;

/**
* @author 懒大王Smile
* @description 针对表【user_collects(用户-收藏表)】的数据库操作Service实现
* @createDate 2024-09-12 22:19:13
*/
@Service
public class UserCollectsServiceImpl extends ServiceImpl<UserCollectsMapper, UserCollects>
    implements UserCollectsService {

}




