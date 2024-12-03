package com.serein.constants;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/13
 * @Time: 11:30
 * @Description:
 */

public interface ErrorInfo {

  String FILE_TYPE_ERROR = "文件类型错误，请上传png文件";
  String ADD_ERROR = "添加失败";

  String UPDATE_ERROR = "更新失败";

  String RELEASED_ERROR = "发布失败";

  String DELETE_ERROR = "删除失败";

  String SYS_ERROR = "系统错误";

  String LOGIN_INFO_ERROR = "邮箱或密码错误";


  String MAIL_EXISTED_ERROR = "该邮箱已被注册";


  String NOT_LOGIN_ERROR = "未登录";

  String PASSWORD_ERROR = "密码错误";

  String USERNAME_ERROR = "用户名错误";


  String NO_DB_DATA = "数据库无该数据";

  String DB_FAIL = "数据库查询失败";

  String NO_AUTH_ERROR = "无权限";

  String PARAMS_ERROR = "请求参数错误";

  String USERNAME_EXISTED_ERROR = "用户名已存在";


  String FILE_SIZE_ERROR = "文件过大";

}
