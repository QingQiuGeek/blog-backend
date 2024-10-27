package com.serein.utils;

import java.io.Serializable;

import com.serein.constants.ErrorCode;
import lombok.Data;

/**
 * 通用返回类
 *返回给前端数据
 * @author 懒大王Smile
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
