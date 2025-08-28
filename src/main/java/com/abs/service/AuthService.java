package com.abs.service;

import com.abs.domain.User;
import com.abs.repository.UserRepository;
import com.abs.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwt;
    private final RefreshTokenService refreshTokenService;

    public Tokens login(String email, String password) {
        User u = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("LOGIN_ERR_USER_NOT_FOUND"));
        if (!passwordEncoder.matches(password, u.getUserPwd()))
            throw new IllegalArgumentException("LOGIN_ERR_INVALID_PASSWORD");

        String sid = jwt.newSessionId();
        String accessToken = jwt.generateAccessToken(u.getUserNo(), u.getUserEmail());
        String refreshToken = jwt.generateRefreshToken(u.getUserNo(), u.getUserEmail(), sid);

        refreshTokenService.save(u.getUserNo(), sid, refreshToken);
        return new Tokens(accessToken, refreshToken, sid, u.getUserNo(), u.getUserEmail());
    }

    public Tokens refresh(String refreshToken, String sid) {
        Long uid = jwt.getUserNo(refreshToken);
        String current = refreshTokenService.get(uid, sid);
        if (current == null || !current.equals(refreshToken)) {
            refreshTokenService.delete(uid, sid);
            throw new IllegalArgumentException("INVALID_REFRESH");
        }
        String email = jwt.getUserEmail(refreshToken);
        String newAccess = jwt.generateAccessToken(uid, email);
        String newRefresh = jwt.generateRefreshToken(uid, email, sid); // 회전
        refreshTokenService.save(uid, sid, newRefresh);
        return new Tokens(newAccess, newRefresh, sid, uid, email);     // ★ 동일 형태 반환
    }

    public void logout(Long userNo, String sid) {
        refreshTokenService.delete(userNo, sid);
    }

    // 간단 DTO
    public record Tokens(
            String accessToken,
            String refreshToken,
            String sessionId,
            Long userNo,
            String userEmail
    ) {}
}
