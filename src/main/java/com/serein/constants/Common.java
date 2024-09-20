package com.serein.constants;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/13
 * @Time: 13:26
 * @Description:
 */

public interface Common {


    //用户登录态key，可以保存在session或者redis
     String USER_LOGIN_STATE="USER_LOGIN_STATE:";

     Long PAGE_SIZE=5L;

     String LOGIN_TOKEN_KEY="login:token:";

    Long LOGIN_TOKEN_TTL=10L;

}
