package com.serein.service.impl;

import static com.serein.constants.Common.SA_TOKEN_USER_ROLE;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpInterface;
import com.serein.mapper.UserMapper;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * @Author: QingQiu
 * @Date: 2025/6/28
 * @Description: 自定义权限认证接口扩展，Sa-Token 将从此实现类获取每个账号拥有的权限码
 */
@Component
public class StpInterfaceImpl implements StpInterface {

  @Resource
  UserMapper userMapper;
  /**
   * 返回一个账号所拥有的权限码集合
   */
  @Override
  public List<String> getPermissionList(Object loginId, String loginType) {
    return List.of();
  }

  /**
   * 返回一个账号所拥有的角色标识集合
   * 可以存储在缓存中
   */
  @Override
  public List<String> getRoleList(Object loginId, String loginType) {
    List<String> roleList = (List<String>)SaManager.getSaTokenDao().getObject(SA_TOKEN_USER_ROLE + loginId);
    if(roleList == null) {
      String userRole = userMapper.getUserRole(Long.valueOf((String)loginId));
      roleList = new ArrayList<>();
      roleList.add(userRole);
      SaManager.getSaTokenDao().setObject(
          SA_TOKEN_USER_ROLE + loginId, roleList, 60*30);
    }
    return roleList;
  }
}
