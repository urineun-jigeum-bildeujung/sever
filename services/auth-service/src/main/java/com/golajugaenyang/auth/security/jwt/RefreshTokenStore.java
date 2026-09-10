package com.golajugaenyang.auth.security.jwt;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenStore {

    private static final String KEY_PREFIX = "RT:";

    private final StringRedisTemplate redisTemplate;

    public void save(Long memberId, String refreshToken, long ttlSeconds) {
        redisTemplate.opsForValue().set(KEY_PREFIX + memberId, refreshToken, Duration.ofSeconds(ttlSeconds));
    }
}
