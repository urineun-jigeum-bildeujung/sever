package com.golajugaenyang.auth.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class PhoneVerificationStore {

    private static final String KEY_PREFIX = "P";
    private static final String REQUEST_COUNT_PREFIX = "PVR:";
    private static final String CONFIRM_COUNT_PREFIX = "PVC:";

    private static final int MAX_REQUESTS_PER_WINDOW = 5;
    private static final int MAX_CONFIRM_ATTEMPTS_PER_WINDOW = 5;
    private static final Duration REQUEST_WINDOW = Duration.ofHours(1);
    private static final Duration CONFIRM_WINDOW = Duration.ofMinutes(5);

    // KEYS[1]=카운터 키, ARGV[1]=윈도우(초). 새로 생성된 카운터(count==1)일 때만 TTL을 같이 건다
    private static final RedisScript<Long> INCR_WITH_EXPIRE_SCRIPT = new DefaultRedisScript<>(
        "local count = redis.call('INCR', KEYS[1]) "
            + "if count == 1 then "
            + "redis.call('EXPIRE', KEYS[1], ARGV[1]) "
            + "end "
            + "return count",
        Long.class
    );

    private final StringRedisTemplate redisTemplate;

    public void issue(String phone, long ttlSeconds){
        redisTemplate.opsForValue().set(KEY_PREFIX + phone, "PENDING", Duration.ofSeconds(ttlSeconds));
    }

    public boolean isValid(String phone) {
        return redisTemplate.hasKey(KEY_PREFIX + phone);
    }

    public boolean allowRequest(String phone) {
        return withinLimit(REQUEST_COUNT_PREFIX + phone, REQUEST_WINDOW, MAX_REQUESTS_PER_WINDOW);
    }

    public boolean allowConfirmAttempt(String phone) {
        return withinLimit(CONFIRM_COUNT_PREFIX + phone, CONFIRM_WINDOW, MAX_CONFIRM_ATTEMPTS_PER_WINDOW);
    }

    private boolean withinLimit(String key, Duration window, int maxCount) {
        Long count = redisTemplate.execute(
            INCR_WITH_EXPIRE_SCRIPT,
            Collections.singletonList(key),
            String.valueOf(window.getSeconds())
        );
        return count != null && count <= maxCount;
    }

}
