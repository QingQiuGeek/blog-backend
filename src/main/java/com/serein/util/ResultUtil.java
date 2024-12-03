package com.serein.util;

import com.serein.constants.ErrorCode;

/**
 * 返回工具类
 *
 * @author 懒大王Smile
 */
public class ResultUtil {

  /**
   * 成功
   *
   * @param data
   * @param <T>
   * @return
   */
  public static <T> BaseResponse<T> success(T data) {
    return new BaseResponse<>(200, data, "ok");
  }

  /**
   * 失败
   *
   * @param errorCode
   * @return
   */
  public static BaseResponse error(ErrorCode errorCode) {
    return new BaseResponse<>(errorCode);
  }

  /**
   * 失败
   *
   * @param errorCode
   * @return
   */
  public static BaseResponse error(ErrorCode errorCode, String message) {
    return new BaseResponse(errorCode.getCode(), null, message);
  }
}
