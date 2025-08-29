package com.abs.service;

import com.abs.domain.User;
import com.abs.repository.UserRepository;
import com.abs.security.JwtUtil;
import com.abs.security.UserPrincipal;
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
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("LOGIN_ERR_USER_NOT_FOUND"));
        if (!passwordEncoder.matches(password, user.getUserPwd()))
            throw new RuntimeException("LOGIN_ERR_INVALID_PASSWORD");

        UserPrincipal principal = UserPrincipal.from(user);

        String sid = jwt.newSessionId();
        String accessToken = jwt.generateAccessToken(principal);
        String refreshToken = jwt.generateRefreshToken(principal, sid);

        refreshTokenService.save(user.getUserNo(), sid, refreshToken);
        return new Tokens(accessToken, refreshToken, sid, user.getUserNo(), user.getUserEmail());
    }

    public Tokens refresh(String refreshToken, String sid) {
        Long uid = jwt.getUserNo(refreshToken);
        String current = refreshTokenService.get(uid, sid);
        if (current == null || !current.equals(refreshToken)) {
            refreshTokenService.delete(uid, sid);
            throw new IllegalArgumentException("INVALID_REFRESH");
        }

        User user = userRepository.findByUserNo(uid);
        UserPrincipal principal = UserPrincipal.from(user);

        String email = jwt.getUserEmail(refreshToken);
        String newAccess = jwt.generateAccessToken(principal);
        String newRefresh = jwt.generateRefreshToken(principal, sid);
        refreshTokenService.save(uid, sid, newRefresh);
        return new Tokens(newAccess, newRefresh, sid, uid, email);
    }

    public void logout(Long userNo, String sid) {
        refreshTokenService.delete(userNo, sid);
    }


    // DTO
    public record Tokens(
            String accessToken,
            String refreshToken,
            String sessionId,
            Long userNo,
            String userEmail
    ) {}
}
