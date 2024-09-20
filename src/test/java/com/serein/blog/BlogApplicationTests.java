package com.serein.blog;

import com.serein.domain.vo.UserVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;

@SpringBootTest
class BlogApplicationTests {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    void contextLoads() {
        UserVO userVO = new UserVO();
        userVO.setUserId(1L);
        userVO.setLevel(1);
        userVO.setUserName("青秋");
        HashMap<Object, Object> hashMap = new HashMap<>();
        stringRedisTemplate.opsForValue().set("CSDN","青秋");
        System.out.println(stringRedisTemplate.opsForValue().get("CSDN"));
    }

}
