package com.golajugaenyang.auth.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class PhoneVerificationStore {

    private static final String KEY_PREFIX = "P";
    private final StringRedisTemplate redisTemplate;

    public void issue(String phone, long ttlSeconds){
        redisTemplate.opsForValue().set(KEY_PREFIX + phone, "PENDING", Duration.ofSeconds(ttlSeconds));
    }

    public boolean isValid(String phone) {
        return redisTemplate.hasKey(KEY_PREFIX + phone);
    }

}
