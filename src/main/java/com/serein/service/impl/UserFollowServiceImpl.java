package com.serein.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.model.entity.UserFollow;
import com.serein.mapper.UserFollowMapper;
import com.serein.service.UserFollowService;
import org.springframework.stereotype.Service;

/**
* @author 懒大王Smile
* @description 针对表【user_follow(用户关注表)】的数据库操作Service实现
* @createDate 2024-10-25 23:11:12
*/
@Service
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow>
    implements UserFollowService {

}




