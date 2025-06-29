package com.serein.util;


import com.serein.model.vo.userVO.LoginUserVO;

/**
 * 用户上下文，保存用户id
 */
public class UserContext {

  private static final ThreadLocal<Long> tl = new ThreadLocal<>();

  public static void saveUser(Long userId) {
    tl.set(userId);
  }

  public static Long getUser() {
    return tl.get();
  }

  public static void removeUser() {
    tl.remove();
  }
}
