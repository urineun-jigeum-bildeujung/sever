package com.golajugaenyang.auth.security.jwt;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenBlacklistPublisher {

    private static final String CHANNEL = "token-blacklist";
    private static final String KEY_PREFIX = "blacklist:";

    private final StringRedisTemplate redisTemplate;

    public void publish(String accessToken, long ttlSeconds) {
        if (ttlSeconds <= 0) {
            return;
        }

        // 재시작 등으로 구독을 놓친 게이트웨이가 복구할 수 있도록, 방송과 별개로 내구성 있게 저장
        redisTemplate.opsForValue().set(KEY_PREFIX + accessToken, "1", Duration.ofSeconds(ttlSeconds));

        String message = accessToken + ":" + ttlSeconds;
        redisTemplate.convertAndSend(CHANNEL, message);
    }

}
