package com.serein.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serein.model.entity.UserFollow;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 懒大王Smile
 * @description 针对表【user_follow(用户关注表)】的数据库操作Mapper
 * @createDate 2024-10-25 23:11:12
 * @Entity com.serein.model.entity.UserFollow
 */
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {

  int getFollowerNum(Long uid);
}




