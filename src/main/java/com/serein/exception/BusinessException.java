package com.serein.exception;

import lombok.Getter;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/12
 * @Time: 23:08
 * @Description: 自定义异常类
 */

@Getter
public class BusinessException extends RuntimeException{

    int ErrorCode;
    public BusinessException(int ErrorCode,String message) {
        super(message);
        this.ErrorCode=ErrorCode;
    }

}
