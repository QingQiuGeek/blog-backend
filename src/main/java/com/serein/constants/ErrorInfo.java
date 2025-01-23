package com.serein.constants;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/13
 * @Time: 11:30
 * @Description:
 */

public interface ErrorInfo {

  String FILE_TYPE_ERROR = "文件类型错误，请上传png文件";

  String OPERATION_ERROR="操作失败";

  String ADD_ERROR = "添加失败";

  String REGISTER_ERROR="注册失败";

  String SYSTEM_BUSY="系统繁忙，请稍后再试";

  String EXECUTION_FULL_ERROR="系统繁忙，请稍后再试";

  String COMMENT_ERROR = "评论失败";

  String UPDATE_ERROR = "更新失败";

  String REDIS_UPDATE_ERROR = "更新失败";

  String PUBLISH_ERROR = "发布失败";

  String TIME_PUBLISH_ERROR = "定时发布失败";

  String CAPTCHA_ERROR="验证码已过期或不存在";

  String DELETE_ERROR = "删除失败";

  String SYS_ERROR = "系统错误";

  String MAIL_EXISTED_ERROR = "该邮箱已被注册";

  String NOT_LOGIN_ERROR = "未登录";

  String MAIL_OR_PASSWORD_ERROR = "邮箱或密码错误";

  String USERNAME_ERROR = "用户名错误";

  String NO_DB_DATA = "数据库无该数据";

  String NO_REGISTER="该用户尚未注册";

  String DB_FAIL = "数据库查询失败";

  String NO_AUTH_ERROR = "无权限";

  String PARAMS_ERROR = "请求参数错误";

  String USERNAME_EXISTED_ERROR = "用户名已存在";

  String FILE_SIZE_ERROR = "文件过大";

  String BAN_ACCOUNT = "该账户被禁用";
}
