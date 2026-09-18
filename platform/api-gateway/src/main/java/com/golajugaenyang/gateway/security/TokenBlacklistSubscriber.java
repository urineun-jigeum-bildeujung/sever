package com.golajugaenyang.gateway.security;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TokenBlacklistSubscriber {

    private static final String CHANNEL = "token-blacklist";
    private static final String KEY_PREFIX = "blacklist:";

    private final TokenBlacklistCache blacklistCache;
    private final ReactiveRedisMessageListenerContainer listenerContainer;
    private final ReactiveStringRedisTemplate reactiveRedisTemplate;

    public TokenBlacklistSubscriber(
        TokenBlacklistCache blacklistCache,
        ReactiveRedisConnectionFactory connectionFactory,
        ReactiveStringRedisTemplate reactiveRedisTemplate
    ) {
        this.blacklistCache = blacklistCache;
        this.listenerContainer = new ReactiveRedisMessageListenerContainer(connectionFactory);
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void subscribe() {
        recoverFromRedis().block();

        listenerContainer.receive(ChannelTopic.of(CHANNEL))
            .doOnNext(message -> handle(message.getMessage()))
            .subscribe();
    }

    private Mono<Void> recoverFromRedis() {
        return reactiveRedisTemplate.scan(ScanOptions.scanOptions().match(KEY_PREFIX + "*").build())
            .flatMap(key -> reactiveRedisTemplate.getExpire(key)
                .filter(ttl -> !ttl.isNegative() && !ttl.isZero())
                .doOnNext(ttl -> {
                    String token = key.substring(KEY_PREFIX.length());
                    blacklistCache.blacklist(token, ttl.getSeconds());
                }))
            .then();
    }

    private void handle(String message) {
        String[] parts = message.split(":");
        if (parts.length != 2) {
            return;
        }

        String token = parts[0];
        long ttlSeconds = Long.parseLong(parts[1]);
        blacklistCache.blacklist(token, ttlSeconds);
    }
}
