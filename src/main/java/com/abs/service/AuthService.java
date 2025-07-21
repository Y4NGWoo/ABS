// src/main/java/com/abs/abs/service/AuthService.java
package com.abs.service;

import com.abs.domain.User;
import com.abs.repository.UserRepository;
import com.abs.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepo, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.jwtUtil  = jwtUtil;
    }

    public void signup(String email, String pw, String nickname) {
        if (userRepo.findByUserEmail(email).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다");
        }
        String hash = encoder.encode(pw);
        User user = User.builder()
                .userEmail(email)
                .userPwd(hash)
                .userName(nickname)
                .regDtm(LocalDateTime.now())
                .build();
        userRepo.save(user);
    }

    public String login(String email, String pw) {
        User u = userRepo.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("이메일이 없습니다"));
        if (!encoder.matches(pw, u.getUserPwd())) {
            throw new RuntimeException("비밀번호가 틀렸습니다");
        }
        // JWT subject로는 이메일 또는 userNo 둘 다 가능
        return jwtUtil.generateToken(u.getUserEmail());
    }
}
