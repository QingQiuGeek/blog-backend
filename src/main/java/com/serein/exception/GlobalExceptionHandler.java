package com.serein.exception;

import com.serein.constants.ErrorCode;
import com.serein.util.BaseResponse;
import com.serein.util.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/12
 * @Time: 23:03
 * @Description: 全局异常处理器
 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public BaseResponse businessExceptionHandler(BusinessException e) {
    log.error("BusinessException", e);
    return ResultUtil.error(e.getErrorCode(), e.getMessage());
  }

  //出现未定义的异常，统一抛出自定义状态码1000
  @ExceptionHandler(RuntimeException.class)
  public BaseResponse runtimeExceptionHandler(RuntimeException e) {
    log.error("RuntimeException", e);
    return ResultUtil.error(ErrorCode.UNEXPECT_ERROR, e.getMessage());
  }
}
