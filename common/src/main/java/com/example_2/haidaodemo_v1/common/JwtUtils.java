package com.example_2.haidaodemo_v1.common;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${haidao.jwt.secret}")
    private String secret;

    @Value("${haidao.jwt.expire}")
    private long expire;

    /**
     * 生成通行证
     */
    public String createToken(Map<String, Object> claims ) {
        return JWT.create()
                .withClaim("user",claims)
                .withExpiresAt(new Date(System.currentTimeMillis()+expire))
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 验证并解析通行证
     */
    public Map<String, Object> parseToken(String token) {
        return JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token)
                .getClaim("user")
                .asMap();
    }
}
