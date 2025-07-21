package com.abs.security;

import com.abs.domain.User;
import com.abs.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends GenericFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepo;

    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepo) {
        this.jwtUtil  = jwtUtil;
        this.userRepo = userRepo;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        String token = resolveToken((HttpServletRequest) req);
        if (token != null) {
            try {
                String email = jwtUtil.validateTokenAndGetSubject(token);
                User user = userRepo.findByUserEmail(email)
                        .orElseThrow();
                // 인증 객체 생성 (authorities는 필요한 경우 채워 주세요)
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        user, null, Collections.emptyList()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException e){
                // 토큰이 유효하지 않거나 사용자 조회 실패 시
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(req, res);
    }

    private String resolveToken(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
