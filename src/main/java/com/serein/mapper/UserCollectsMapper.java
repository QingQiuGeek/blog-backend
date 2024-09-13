package com.serein.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serein.domain.entity.UserCollects;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 懒大王Smile
* @description 针对表【user_collects(用户-收藏表)】的数据库操作Mapper
* @createDate 2024-09-12 22:19:13
* @Entity com.serein.domain.entity.UserCollects
*/

@Mapper
public interface UserCollectsMapper extends BaseMapper<UserCollects> {

}




