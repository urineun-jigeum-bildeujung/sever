package com.golajugaenyang.gateway.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class TokenBlacklistCache {

    private final Cache<String, Long> cache = Caffeine.newBuilder()
        .expireAfter(new Expiry<String, Long>() {
            @Override
            public long expireAfterCreate(String token, Long ttlSeconds, long currentTime) {
                return TimeUnit.SECONDS.toNanos(ttlSeconds);
            }

            @Override
            public long expireAfterUpdate(String token, Long ttlSeconds, long currentTime, long currentDuration) {
                return TimeUnit.SECONDS.toNanos(ttlSeconds);
            }

            @Override
            public long expireAfterRead(String token, Long ttlSeconds, long currentTime, long currentDuration) {
                return currentDuration;
            }
        })
        .build();

    public void blacklist(String token, long ttlSeconds) {
        if (ttlSeconds <= 0) {
            return;
        }
        cache.put(token, ttlSeconds);
    }

    public boolean isBlacklisted(String token) {
        return cache.getIfPresent(token) != null;
    }
}
