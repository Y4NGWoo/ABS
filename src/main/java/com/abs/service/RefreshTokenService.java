package com.abs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redis;

    private String key(Long userNo, String sessionId) {
        return "refresh:" + userNo + ":" + sessionId;
    }

    public void save(Long userNo, String sessionId, String refresh) {
        redis.opsForValue().set(key(userNo, sessionId), refresh, Duration.ofDays(14));
    }

    public String get(Long userNo, String sessionId) {
        return redis.opsForValue().get(key(userNo, sessionId));
    }

    public void delete(Long userNo, String sessionId) {
        redis.delete(key(userNo, sessionId));
    }

    public void deleteAll(Long userNo) {
        // 간단히 Scan 기반 삭제 (운영에선 Lua/ScanCursor로 안전 삭제 권장)
        var conn = redis.getConnectionFactory().getConnection();
        try (var cursor = conn.scan(org.springframework.data.redis.core.ScanOptions.scanOptions()
                .match(("refresh:" + userNo + ":*")).count(1000).build())) {
            while (cursor.hasNext()) conn.keyCommands().del(cursor.next());
        }
    }
}
