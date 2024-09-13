package com.serein.constants;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/13
 * @Time: 13:09
 * @Description:
 */

public interface ErrorCode {

    //未定义异常码
    int UNEXPECTED_ERROR=1000;

    //请求参数错误码
    int PARAMS_ERROR=4000;

    //未登录错误码
    int NOT_LOGIN_ERROR=40100;

    //无权限错误码
    int NO_AUTH=40200;

    //数据库查找失败
    int NO_DATA=40300;

}
