package com.serein.util;

import com.serein.constants.ErrorCode;

/**
 * 返回工具类
 * @author 懒大王Smile
 */
public class R {

  /**
   * 成功
   * @param data
   * @param <T>
   * @return
   */
  public static <T> BR<T> ok(T data) {
    return new BR<>(200, data, "ok");
  }

  /**
   * 失败
   *
   * @param errorCode
   * @return
   */
  public static BR error(ErrorCode errorCode) {
    return new BR<>(errorCode);
  }

  /**
   * 失败
   *
   * @param errorCode
   * @return
   */
  public static BR error(ErrorCode errorCode, String message) {
    return new BR(errorCode.getCode(), null, message);
  }
}
