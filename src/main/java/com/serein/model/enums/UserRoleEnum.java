package com.serein.model.enums;

import org.apache.commons.lang3.ObjectUtils;

/**
 * @Author:懒大王Smile
 * @Date: 2024/10/27
 * @Time: 14:16
 * @Description:
 */

public enum UserRoleEnum {


  USER("普通用户", "user"),
  ADMIN("管理员", "admin"),
  BAN("封号", "ban");

  private final String text;
  private final String role;

  UserRoleEnum(String text, String role) {
    this.role = role;
    this.text = text;
  }

  /**
   * 根据 value 获取枚举
   *
   * @param role
   * @return
   */
  public static UserRoleEnum getEnumByRole(String role) {
    if (ObjectUtils.isEmpty(role)) {
      return null;
    }
    for (UserRoleEnum anEnum : UserRoleEnum.values()) {
      if (anEnum.role.equals(role)) {
        return anEnum;
      }
    }
    return null;
  }


}
