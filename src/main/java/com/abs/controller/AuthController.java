package com.abs.controller;

import com.abs.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        authService.signup(req.getEmail(), req.getPassword(), req.getNickname());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        String token = authService.login(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @Data
    static class SignupRequest {
        private String email;
        private String password;
        private String nickname;
    }

    @Data @AllArgsConstructor
    static class LoginResponse {
        private String accessToken;
    }

    @Data
    static class LoginRequest {
        private String email;
        private String password;
    }
}
