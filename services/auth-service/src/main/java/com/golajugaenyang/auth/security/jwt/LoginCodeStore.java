package com.golajugaenyang.auth.security.jwt;

import com.golajugaenyang.auth.application.recods.LoginCodePayload;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class LoginCodeStore {

    private static final String KEY_PREFIX = "login-code:";
    private static final Duration TTL = Duration.ofSeconds(60);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void save(String code, LoginCodePayload payload) {
        String json = objectMapper.writeValueAsString(payload);
        redisTemplate.opsForValue().set(KEY_PREFIX + code, json, TTL);
    }

    public Optional<LoginCodePayload> consume(String code) {
        String json = redisTemplate.opsForValue().getAndDelete(KEY_PREFIX + code);
        if (json == null) {
            return Optional.empty();
        }
        return Optional.of(objectMapper.readValue(json, LoginCodePayload.class));
    }
}
