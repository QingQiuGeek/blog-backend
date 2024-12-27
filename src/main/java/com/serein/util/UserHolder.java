package com.serein.util;


import com.serein.model.vo.userVO.LoginUserVO;

/**
 * @author 懒大王Smile
 */
public class UserHolder {

  private static final ThreadLocal<LoginUserVO> tl = new ThreadLocal<>();

  public static void saveUser(LoginUserVO user) {
    tl.set(user);
  }

  public static LoginUserVO getUser() {
    return tl.get();
  }

  public static void removeUser() {
    tl.remove();
  }
}
