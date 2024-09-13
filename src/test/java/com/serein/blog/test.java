package com.serein.blog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/13
 * @Time: 0:57
 * @Description:
 */

@SpringBootTest
public class test {

    @Test
    public void test1(){
        int a=1/0;
        System.out.println(a);
    }
}
