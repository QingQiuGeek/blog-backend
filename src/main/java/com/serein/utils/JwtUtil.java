package com.serein.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;

import java.util.Calendar;
import java.util.Map;

public class JwtUtil {

    /**
     * 生成token
     */
    @Value("${custom.token}")
    private static String SING;

    public static String createJWT(Map<String, String> map) {

        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND, 10);
//        instance.add(Calendar.MINUTE, 10);

        JWTCreator.Builder builder = JWT.create();

        // payload
        map.forEach(builder::withClaim);

        return builder.withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256(SING));
    }

    /**
     * 验证token  合法性
     */
    public static DecodedJWT verifyJWT(String token) {
        return JWT.require(Algorithm.HMAC256(SING)).build().verify(token);
    }

}



