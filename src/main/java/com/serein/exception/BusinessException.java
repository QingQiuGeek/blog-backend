package com.serein.exception;

import com.serein.constants.ErrorCode;
import lombok.Getter;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/12
 * @Time: 23:08
 * @Description: 自定义异常类
 */
//spring默认回滚运行时异常和Error  https://blog.csdn.net/hanjiaqian/article/details/120501741
@Getter
public class BusinessException extends RuntimeException {

  ErrorCode errorCode;

  public BusinessException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

}
