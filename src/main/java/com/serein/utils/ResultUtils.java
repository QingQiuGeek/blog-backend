package com.serein.utils;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public  class ResultUtils {

    private int code;
    private String message;
    private Object data;
    private Long total;

    public ResultUtils(int code, String message, Object data, Long total) {
        this.code=code;
        this.message = message;
        this.data = data;
        this.total = total;
    }

    /**
     * 成功一律返回200
     * @param message
     * @param data
     * @return
     */
    public static ResultUtils ok(String message,Object data){
        return new ResultUtils(200, message, data, null);
    }
    public static ResultUtils ok(String message,List<?> data, Long total){
        return new ResultUtils(200, message, data, total);
    }
    public static ResultUtils ok(String message){
        return new ResultUtils(200, message, null,null);
    }

    /**
     * 失败则返回自定义状态码
     * @param errorMsg
     * @param ErrorCode
     * @return
     */
    public static ResultUtils fail(int ErrorCode,String errorMsg){
        return new ResultUtils(ErrorCode, errorMsg, null, null);
    }


}
