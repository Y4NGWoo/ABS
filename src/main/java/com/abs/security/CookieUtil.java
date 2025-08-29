package com.abs.security;

import org.springframework.http.ResponseCookie;

public class CookieUtil {

    // Access 토큰: 전역 경로(/), 짧은 만료
    public static ResponseCookie accessCookie(String token, boolean secure) {
        return ResponseCookie.from("ACCESS_TOKEN", token)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")     // Strict로 두면 일부 리디렉션 시 문제될 수 있어 Lax 권장
                .path("/")
                .maxAge(15 * 60)     // 15분
                .build();
    }

    // Refresh 토큰: /api/auth로 범위 제한, 긴 만료
    public static ResponseCookie refreshCookie(String token, boolean secure) {
        return ResponseCookie.from("REFRESH_TOKEN", token)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path("/api/auth")   // 리프레시/로그아웃 엔드포인트에만 자동 전송
                .maxAge(14L * 24 * 60 * 60) // 14일
                .build();
    }

    // 쿠키 제거용
    public static ResponseCookie clear(String name, boolean secure, String path) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path(path)
                .maxAge(0)
                .build();
    }
}