package com.abs.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private final byte[] key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = secret.getBytes(StandardCharsets.UTF_8);
    }

    public String generateAccessToken(UserPrincipal principal) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(principal.getUserEmail())
                .claim("uid", principal.getUserNo())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(60 * 15))) // 15분
                .signWith(Keys.hmacShaKeyFor(key), SignatureAlgorithm.HS256)
                .compact();
    }

    // 세션ID를 외부에서 주입(로그인 시 생성)하여 리턴도 함께 사용
    public String generateRefreshToken(UserPrincipal principal, String sessionId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(principal.getUserEmail())
                .claim("uid", principal.getUserNo())
                .claim("sid", sessionId)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(60L * 60 * 24 * 14))) // 14일
                .signWith(Keys.hmacShaKeyFor(key), SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(key))
                .build()
                .parseClaimsJws(token);
    }

    public boolean validateToken(String token) {
        try { parse(token); return true; }
        catch (JwtException | IllegalArgumentException e) { return false; }
    }

    public Long getUserNo(String token) { return parse(token).getBody().get("uid", Number.class).longValue(); }
    public String getUserEmail(String token) { return parse(token).getBody().getSubject(); }
    public String getSessionId(String token) { return parse(token).getBody().get("sid", String.class); }

    // 로그인 시 사용할 세션ID 생성기
    public String newSessionId() { return UUID.randomUUID().toString(); }

}
