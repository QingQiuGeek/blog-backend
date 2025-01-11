package com.serein.util;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 懒大王Smile
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt.token")
public class JwtHelper {

  private static long tokenExpiration;
  //有效时间,单位毫秒 1000毫秒 == 1秒
  private static String tokenSignKey;
  //签名秘钥

  //生成token字符串
  public static String createToken(Long userId,String role) {
    String token = Jwts.builder()
        .setSubject("USER")
        .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration * 1000 * 60))
        .claim("userId", userId)
        .claim("role",role)
        .signWith(SignatureAlgorithm.HS256, tokenSignKey)
        .compressWith(CompressionCodecs.GZIP)
        .compact();
    return token;
  }

  //从token字符串获取userid
  public static Long getUserId(String token) {
    if (StringUtils.isEmpty(token)) {
      return null;
    }
    Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
    Claims claims = claimsJws.getBody();
    Integer userId = (Integer) claims.get("userId");
    return userId.longValue();
  }

  public static String getUserRole(String token) {
    if (StringUtils.isEmpty(token)) {
      return null;
    }
    Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
    Claims claims = claimsJws.getBody();
    return  (String)claims.get("role");
  }


  //判断token是否过期
  public static boolean isExpiration(String token) {
    try {
      boolean isExpire = Jwts.parser()
          .setSigningKey(tokenSignKey)
          .parseClaimsJws(token)
          .getBody()
          .getExpiration().before(new Date());
      //没有过期，有效，返回false
      return isExpire;
    } catch (Exception e) {
      //过期出现异常，返回true
      return true;
    }
  }
}
