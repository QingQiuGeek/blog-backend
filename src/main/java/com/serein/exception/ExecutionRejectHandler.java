package com.serein.exception;

import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author:懒大王Smile
 * @Date: 2024/12/17
 * @Time: 15:22
 * @Description: 自定义线程池拒绝策略
 */

public class ExecutionRejectHandler implements RejectedExecutionHandler {

  @Override
  public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
    throw new BusinessException(ErrorCode.EXECUTION_FULL_ERROR, ErrorInfo.EXECUTION_FULL_ERROR);
  }
}
