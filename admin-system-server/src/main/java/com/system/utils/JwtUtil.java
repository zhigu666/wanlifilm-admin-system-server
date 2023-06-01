package com.system.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "token.jwt") //用于读取application.yaml配置文件，获得参数值
//prefix = "token.jwt"  读取的是配置文件中jwt的参数值
public class JwtUtil {
    private Long expire;
    private String secret;
    private String header;

    public String createToken(String username){
        Date nowDate =new Date();
        Date expireDate =new Date(nowDate.getTime()+1000*expire);
        log.info("秘钥--{}",secret);
         return Jwts.builder()
                .setHeaderParam("typ","JWT")
                .setSubject(username)
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512,secret)
                .compact();
    }

    //解析jwt 登陆成功后每次对服务器的访问都需要携带jwt
    //如果对象不为空则成功，为空就是解析失败
    public Claims getClaimsByToken(String jwt){
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(jwt)
                .getBody();
    }

    public boolean isTokenExpired(Claims claims){
        return claims.getExpiration().before(new Date());
    }
}
