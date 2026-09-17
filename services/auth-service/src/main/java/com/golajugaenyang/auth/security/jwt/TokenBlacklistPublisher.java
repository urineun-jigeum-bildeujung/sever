package com.golajugaenyang.auth.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenBlacklistPublisher {

    private static final String CHANNEL = "token-blacklist";

    private final StringRedisTemplate redisTemplate;

    public void publish(String accessToken, long ttlSeconds) {
        String message = accessToken + ":" + ttlSeconds;
        redisTemplate.convertAndSend(CHANNEL, message);
    }

}
