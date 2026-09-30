package com.golajugaenyang.gateway.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class TokenBlacklistCache {

    private final Cache<String, Long> cache;

    public TokenBlacklistCache(MeterRegistry meterRegistry) {
        this.cache = Caffeine.newBuilder()
            // hit/miss/eviction 통계를 실제로 수집하려면 recordStats()가 필수 —
            // 없으면 Caffeine의 stats()가 항상 0만 반환한다.
            .recordStats()
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

        CaffeineCacheMetrics.monitor(meterRegistry, cache, "token-blacklist");
    }

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
