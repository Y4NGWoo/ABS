package com.abs.controller;

import com.abs.security.JwtUtil;
import com.abs.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwt;

    // 로그인: Access는 바디, Refresh/sid는 HttpOnly 쿠키
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq req) {
        var t = authService.login(req.email(), req.password());

        ResponseCookie refresh = ResponseCookie.from("refresh", t.refreshToken())
                .httpOnly(true).secure(false) // 로컬 http 개발용. 운영은 true + HTTPS
                .sameSite("Strict").path("/api/auth").maxAge(60L * 60 * 24 * 14).build();
        ResponseCookie sid = ResponseCookie.from("sid", t.sessionId())
                .httpOnly(true).secure(false)
                .sameSite("Strict").path("/api/auth").maxAge(60L * 60 * 24 * 14).build();

        return ResponseEntity.ok()
                .header("Set-Cookie", refresh.toString())
                .header("Set-Cookie", sid.toString())
                .body(new TokenRes(t.accessToken()));
    }

    // 재발급(회전): 쿠키의 refresh + sid 사용
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue("refresh") String refresh,
                                     @CookieValue("sid") String sid) {
        var t = authService.refresh(refresh, sid);

        ResponseCookie newRefresh = ResponseCookie.from("refresh", t.refreshToken())
                .httpOnly(true).secure(false).sameSite("Strict").path("/api/auth")
                .maxAge(60L * 60 * 24 * 14).build();
        return ResponseEntity.ok()
                .header("Set-Cookie", newRefresh.toString())
                .body(new TokenRes(t.accessToken()));
    }

    // 단일 기기 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue("sid") String sid,
                                    HttpServletRequest request) {
        String access = request.getHeader("Authorization");
        Long userNo = (access != null && access.startsWith("Bearer "))
                ? jwt.getUserNo(access.substring(7)) : null;
        if (userNo != null) authService.logout(userNo, sid);

        // 쿠키 만료
        ResponseCookie expire = ResponseCookie.from("refresh","").maxAge(0)
                .httpOnly(true).secure(false).sameSite("Strict").path("/api/auth").build();
        ResponseCookie expireSid = ResponseCookie.from("sid","").maxAge(0)
                .httpOnly(true).secure(false).sameSite("Strict").path("/api/auth").build();

        return ResponseEntity.ok()
                .header("Set-Cookie", expire.toString())
                .header("Set-Cookie", expireSid.toString())
                .build();
    }

    // 인증 확인용
    @GetMapping("/whoami")
    public ResponseEntity<?> whoami(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(new WhoAmIRes(auth.getName())); // email
    }

    public record LoginReq(String email, String password) {}
    public record TokenRes(String accessToken) {}
    public record WhoAmIRes(String email) {}
}
