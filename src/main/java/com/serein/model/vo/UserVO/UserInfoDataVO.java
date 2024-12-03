package com.serein.model.vo.UserVO;

import lombok.Data;

/**
 * @Author:懒大王Smile
 * @Date: 2024/11/17
 * @Time: 15:25
 * @Description: 展示在个人主页的个人信息
 */


@Data
public class UserInfoDataVO {

  Integer followerNum = 0;
  Integer collectNum = 0;
  Integer passageNum = 0;
  Integer followNum = 0;
  Integer thumbNum = 0;
}
