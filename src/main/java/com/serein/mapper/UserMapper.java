package com.serein.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serein.model.entity.User;
import com.serein.model.vo.CommentVO.CommentUserInfoVO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author 懒大王Smile
 * @description 针对表【user(用户表)】的数据库操作Mapper
 * @createDate 2024-09-12 22:19:13
 * @Entity com.serein.domain.entity.User
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {

  List<CommentUserInfoVO> getCommentUserInfoByUserIdList(
      @Param("commentUserIdList") List<Long> commentUserIdList);

  @Update("update blog.user set ipAddress = #{ipAddress} where userId = #{userId}")
  void updateIpAddress(String ipAddress, Long userId);

  @Update("update blog.user set avatarUrl=#{avatarUrl} where userId=#{userId}")
  boolean updateAvatar( @Param("userId") Long userId,@Param("avatarUrl") String avatarUrl);

  @Select("select avatarUrl,userName from blog.user where userId=#{userId}")
  User getAuthorInfo(Long userId);
}




