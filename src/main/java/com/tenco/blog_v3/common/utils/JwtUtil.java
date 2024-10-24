package com.tenco.blog_v3.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.tenco.blog_v3.user.User;

import java.util.Date;

/**
 * JWT 토큰의 생성 및 검증을 위한 util 클래스
 * 여기서는 알고리즘 HMAC512 알고리즘을 사요한다.
 */
public class JwtUtil {

    /**
     * 주어진 사용자 정보(User)로 JWT 토큰을 생성한다.
     * return 생성된 JWT String
     */
    public static String create(User user) {
        return JWT.create()
                // 헤더
                .withSubject("blog")
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                // 페이로드 - 데이터 조각 클레임(사용자 id, 사용자 이름)
                .withClaim("id", user.getId())
                .withClaim("username", user.getUsername())
                // 서명
                .sign(Algorithm.HMAC512("tencoding"));
    }

    public static User verify(String token) {
        // jwt 디코딩
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512("tencoding"))
                .build().verify(token);

        // 검증된 JWT 에서 사용자 ID와 이름 추출
        int id = decodedJWT.getClaim("id").asInt();
        String username = decodedJWT.getClaim("username").asString();

        return User.builder().id(id).username(username).build();
    }
    
}
