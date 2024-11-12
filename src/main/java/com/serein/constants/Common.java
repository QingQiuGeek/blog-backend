package com.serein.constants;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/13
 * @Time: 13:26
 * @Description:
 */

public interface Common {


    //用户登录态key，可以保存在session或者redis
//     String USER_LOGIN_STATE="USER_LOGIN_STATE:";

     Long PAGE_SIZE=10L;

     String LOGIN_TOKEN_KEY="Blog:login:token:";

    String PASSAGE_THUMB_KEY="Blog:passage:thumb:";

    String PASSAGE_COLLECT_KEY="Blog:passage:collect:";

    String USER_FOLLOW_KEY="Blog:user:follow:";

    String USER_SIGNIN_KEY="Blog:user:signin:";

    String USER_REGISTER_CODE_KEY="Blog:user:registerCode:";

    Long REGISTER_CODE_TTL=1L;
     //单位min
    Long LOGIN_TOKEN_TTL=100L;

    String EMAIL_REGEX = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";

}
