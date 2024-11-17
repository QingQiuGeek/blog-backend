package com.serein.blog;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serein.constants.Common;
import com.serein.mapper.PassageMapper;
import com.serein.model.dto.passageDTO.AddPassageDTO;
import com.serein.model.dto.passageDTO.PassageDTO;
import com.serein.model.dto.passageDTO.UpdatePassageDTO;
import com.serein.model.entity.User;
import com.serein.model.vo.PassageVO.PassageContentVO;
import com.serein.model.vo.UserVO.UserVO;
import com.serein.service.UserService;
import com.serein.service.impl.UserServiceImpl;
import com.serein.utils.JwtHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class BlogApplicationTests {

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @Autowired
    UserServiceImpl userService;
    @Autowired
    UserServiceImpl userServiceImpl;
    @Autowired
    PassageMapper passageMapper;

    AddPassageDTO addPassageDTO = new AddPassageDTO();
    UpdatePassageDTO updatePassageDTO=new UpdatePassageDTO();

    @Test
    void doClass(){
        getDtoClass(addPassageDTO);
        getDtoClass(updatePassageDTO);

    }

    void getDtoClass(PassageDTO passageDTO){
        System.out.println(passageDTO.getClass());
    }

    @Test
    void getPassageContent(){
        PassageContentVO passageContentByPid = passageMapper.getPassageContentByPid(1L,1849451260659367936L);
        System.out.println(passageContentByPid.toString());
    }
@Test
void dto(){
    PassageDTO passageDTO = new PassageDTO();
    passageDTO.setTitle("哈哈哈");
    System.out.println(passageDTO.toString());

    AddPassageDTO addPassageDTO = new AddPassageDTO();
    addPassageDTO.setContent("content");
    System.out.println(addPassageDTO.toString());
    System.out.println(addPassageDTO.getContent());

    }
    //传入重复的id，查出的结果不重复
    @Test
    void selectBatchById(){
        List<Long> idlist=new ArrayList<>();
        Collections.addAll(idlist, 10L,11L,12L,13L,10L,11L);
        List<User> userList = userService.listByIds(idlist);
        System.out.println(userList);
    }
    @Test
    void jsonToList(){
        List<String> list=new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        System.out.println("List<String>："+list);
        String jsonStr1 = JSONUtil.toJsonStr(list);
        System.out.println("List<String>转换成json："+jsonStr1);
        JSONArray objects = JSONUtil.parseArray(jsonStr1);
        List<String> list1 = JSONUtil.toList(objects, String.class);
        System.out.println("Json转换成List<String>："+list1);
    }
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


    @Test
    void blank(){
        String str1="";
        String str2=null;
        String str3;
        String str4="   ";
        if (StringUtils.isAnyBlank(str1,str2,str4))
        {
            System.out.println("blank");
        }
        if (StringUtils.isAnyEmpty(str1,str2,str4))
        {
            System.out.println("empty");
        }
    }

    @Autowired
    private JwtHelper jwtHelper;

    @Test
    public void test(){
        //生成 传入用户标识
        String token = jwtHelper.createToken(5L);
        System.out.println("token = " + token);

        //解析用户标识
        int userId = jwtHelper.getUserId(token).intValue();
        System.out.println("userId = " + userId);

        //校验是否到期! false 未到期 true到期
        boolean expiration = jwtHelper.isExpiration(token);
        System.out.println("expiration = " + expiration);

    }

    public void t1(UserVO userVO){
        userVO.setUserName("serein");
        userVO.setSex(1);
    }

    @Test
    public void t2(){
        UserVO userVO = new UserVO();
        t1(userVO);
        System.out.println(userVO.toString());
    }

    @Test
    public void uuidTest(){
        String token1 = UUID.randomUUID().toString(false);
        String token2 = UUID.randomUUID().toString(true);
        String token3 = UUID.randomUUID(true).toString(false);
        String token4 = UUID.randomUUID(false).toString(false);

        System.out.println(token1);
        System.out.println(token2);
        System.out.println(token3);
        System.out.println(token4);
    }

    @Test
    public void redisMapTest(){

        String token = UUID.randomUUID().toString(false);
        UserVO userVO = new UserVO();
        userVO.setMail("3343@qq.com");
        userVO.setUserName("tom");
        Map<String, Object> map = BeanUtil.beanToMap(userVO, String.valueOf(new HashMap<>()));
        String tokenKey= Common.LOGIN_TOKEN_KEY+token;
        stringRedisTemplate.opsForHash().putAll(tokenKey,map);
//        stringRedisTemplate.opsForValue().set(tokenKey,"userVO");
//        String s = stringRedisTemplate.opsForValue().get(Common.LOGIN_TOKEN_KEY+token);
        //userVO
//        System.out.println(s);

        //null
//        String s2 = stringRedisTemplate.opsForValue().get(Common.LOGIN_TOKEN_KEY+token);
//        System.out.println(s);

        stringRedisTemplate.opsForValue().set(Common.LOGIN_TOKEN_KEY,token);

    }

}
