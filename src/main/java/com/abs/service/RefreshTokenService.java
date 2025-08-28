package com.abs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redis;

    private String key(Long userId, String sessionId) {
        return "refresh:" + userId + ":" + sessionId;
    }

    public void save(Long userId, String sessionId, String refresh) {
        redis.opsForValue().set(key(userId, sessionId), refresh, Duration.ofDays(14));
    }

    public String get(Long userId, String sessionId) {
        return redis.opsForValue().get(key(userId, sessionId));
    }

    public void delete(Long userId, String sessionId) {
        redis.delete(key(userId, sessionId));
    }

    public void deleteAll(Long userId) {
        // 간단히 Scan 기반 삭제 (운영에선 Lua/ScanCursor로 안전 삭제 권장)
        var conn = redis.getConnectionFactory().getConnection();
        try (var cursor = conn.scan(org.springframework.data.redis.core.ScanOptions.scanOptions()
                .match(("refresh:" + userId + ":*")).count(1000).build())) {
            while (cursor.hasNext()) conn.keyCommands().del(cursor.next());
        }
    }
}
