package com.abs.controller;

import com.abs.security.CookieUtil;
import com.abs.service.AuthService;
import com.abs.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    //테스트용
    private final Boolean localSecure = false;

    // 로그인: Access는 바디, Refresh/sid는 HttpOnly 쿠키 > 전부 쿠키로
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq req, HttpServletRequest httpReq) {
        var result = authService.login(req.userEmail(), req.userPwd());

        //boolean secure = httpReq.isSecure(); 운영시 전환

        return ResponseEntity.ok()
                .header("Set-Cookie", CookieUtil.accessCookie(result.accessToken(), localSecure).toString())
                .header("Set-Cookie", CookieUtil.refreshCookie(result.refreshToken(), localSecure).toString())
                .body(Map.of("success", true));
    }

    // 재발급(회전): 쿠키의 refresh + sid 사용
    @PostMapping("/api/auth/refresh")
    public ResponseEntity<?> refresh(@CookieValue("REFRESH_TOKEN") String refreshToken,
                                     @CookieValue("SID") String sid, HttpServletRequest httpReq) { // 세션 식별자 쿠키로 쓰면 더 안전
        var res = authService.refresh(refreshToken, sid);
        //boolean secure = httpReq.isSecure();

        var resp = ResponseEntity.ok()
                .header("Set-Cookie", CookieUtil.accessCookie(res.accessToken(), localSecure).toString());

        if (res.refreshToken() != null) {
            resp = resp.header("Set-Cookie", CookieUtil.refreshCookie(res.refreshToken(), localSecure).toString());
        }
        return resp.body(Map.of("success", true));
    }

    // 단일 기기 로그아웃
    @PostMapping("/api/auth/logout")
    public ResponseEntity<?> logout(
            @AuthenticationPrincipal UserPrincipal principal, HttpServletRequest httpReq,
            @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken) {

        // 해당 세션/기기의 refresh 무효화 (Redis 삭제)
        authService.logout(principal.getUserNo(), refreshToken);

        //boolean secure = httpReq.isSecure();

        return ResponseEntity.ok()
                .header("Set-Cookie", CookieUtil.clear("ACCESS_TOKEN", localSecure, "/").toString())
                .header("Set-Cookie", CookieUtil.clear("REFRESH_TOKEN", localSecure, "/api/auth").toString())
                .body(Map.of("success", true));
    }


    // 인증 확인용
    @GetMapping("/api/auth/whoAmI")
    public ResponseEntity<?> whoAmI(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(new WhoAmIRes(
                principal.getUserNo(),
                principal.getUserEmail(),
                principal.getUserName()
        ));
    }

    public record LoginReq(String userEmail, String userPwd) {}
    public record TokenRes(String accessToken) {}
    public record WhoAmIRes(Long userNo, String userEmail, String userName) {}
}
