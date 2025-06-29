package com.serein.exception;

import com.serein.constants.ErrorCode;
import com.serein.util.BR;
import com.serein.util.R;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/12
 * @Time: 23:03
 * @Description: 全局异常处理器
 */

@Hidden
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  //@ExceptionHandler用来处理controller级别的异常
  @ExceptionHandler(BusinessException.class)
  public BR businessExceptionHandler(BusinessException e) {
    log.error("BusinessException", e);
    return R.error(e.getErrorCode(), e.getMessage());
  }

  //出现未定义的异常，统一抛出自定义状态码1000
  @ExceptionHandler(RuntimeException.class)
  public BR runtimeExceptionHandler(RuntimeException e) {
    log.error("RuntimeException", e);
    return R.error(ErrorCode.UNEXPECT_ERROR, e.getMessage());
  }
}
