package com.golajugaenyang.auth.security.jwt;

import java.time.Duration;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenStore {

    private static final String KEY_PREFIX = "RT:";

    // KEYS[1]=저장 키, ARGV[1]=기존에 제시된 토큰, ARGV[2]=새로 발급할 토큰, ARGV[3]=새 TTL(초)
    // 현재 저장된 값이 제시된 토큰과 같을 때만 새 토큰으로 교체
    private static final RedisScript<Long> ROTATE_SCRIPT = new DefaultRedisScript<>(
        "if redis.call('GET', KEYS[1]) == ARGV[1] then "
            + "redis.call('SET', KEYS[1], ARGV[2], 'EX', ARGV[3]) "
            + "return 1 "
            + "else "
            + "return 0 "
            + "end",
        Long.class
    );

    private final StringRedisTemplate redisTemplate;

    public void save(Long memberId, String refreshToken, long ttlSeconds) {
        redisTemplate.opsForValue().set(KEY_PREFIX + memberId, refreshToken, Duration.ofSeconds(ttlSeconds));
    }

    public boolean rotate(Long authId, String oldRefreshToken, String newRefreshToken, long ttlSeconds) {
        Long result = redisTemplate.execute(
            ROTATE_SCRIPT,
            Collections.singletonList(KEY_PREFIX + authId),
            oldRefreshToken, newRefreshToken, String.valueOf(ttlSeconds)
        );
        return result != null && result == 1L;
    }

    public void delete(Long authId) {
        redisTemplate.delete(KEY_PREFIX + authId);
    }
}
