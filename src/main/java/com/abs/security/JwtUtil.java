package com.abs.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 1) 시크릿 키 (최소 32바이트 이상)
    private static final String SECRET_KEY = "여기에_32바이트_이상_랜덤_문자열을_넣으세요!";
    // 2) 토큰 만료 시간 (예: 24시간)
    private static final long EXPIRATION_MS = 1000L * 60 * 60 * 24;

    // 3) JJWT용 서명 키 객체
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * 주어진 subject(e.g. email or userId)로 JWT를 생성합니다.
     */
    public String generateToken(String subject) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(subject)                         // payload: sub 클레임
                .setIssuedAt(now)                            // payload: iat 클레임
                .setExpiration(new Date(now.getTime() + EXPIRATION_MS)) // exp 클레임
                .signWith(key, SignatureAlgorithm.HS256)     // HS256 알고리즘, 위키키로 서명
                .compact();
    }

    /**
     * 토큰을 파싱·검증하고, payload의 subject를 반환합니다.
     * 만료되었거나 위조된 토큰인 경우 예외를 던집니다.
     */
    public String validateTokenAndGetSubject(String token) {
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)  // 서명 검증용 키
                .build()
                .parseClaimsJws(token);

        // 검증에 성공했다면 subject 반환
        return claims.getBody().getSubject();
    }
}
