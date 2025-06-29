package com.serein.constants;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/13
 * @Time: 13:26
 * @Description: 通用常量字符串
 */

public interface Common {

  String SA_TOKEN_USER_ROLE = "sa-token:role:";

  String INDEX_NAME="passage_v2";

  String HOT_PASSAGE_KEY="hotPassage";

  String HOT_IP_KEY="hotIp";

  Long HOT_PASSAGE_DURATION=60L;

  String IMG_UPLOAD_DIR = "/uploadImg/";

  //限制可上传的图片大小
  int IMG_SIZE_LIMIT = 1;

  //限制可上传的图片大小单位
  String IMG_SIZE_UNIT = "M";

  //前端category页随机抽取的标签数量
  Integer TAGS_NUM = 50;

  String BLOG_CACHE_PREFIX="Blog:cache:";

  String TIME_PUBLISH_KEY="Blog:timePublish:";

  String LOGIN_TOKEN_KEY = "Blog:login:token:";

  String PASSAGE_THUMB_KEY = "Blog:passage:thumb:";

  String PASSAGE_COLLECT_KEY = "Blog:passage:collect:";

  String TOP_COLLECT_PASSAGE = "Blog:passage:topCollect:";

  //用户的关注信息存在redis中，登录用户的Id为key，被关注的用户Id为value。我关注了谁
  String USER_FOLLOW_KEY = "Blog:user:follow:";

  String USER_REGISTER_CAPTCHA_KEY = "Blog:user:registerCaptcha:";

  Long REGISTER_CAPTCHA_TTL = 1L;

  //单位min
  Long LOGIN_TOKEN_TTL = 60L;

  //邮箱正则
  String EMAIL_REGEX = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";

  //https://goregex.cn/
  //用户名匹配，仅支持中文、英文、数字、下划线，长度2-6
  String USERNAME_REGEX = "^[\\u4E00-\\u9FA5A-Za-z0-9_]{2,6}$";

  //必须包含字母和数字，不能使用特殊字符
  String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]{6,10}$";
}
