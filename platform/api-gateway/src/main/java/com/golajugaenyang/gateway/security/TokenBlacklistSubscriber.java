package com.golajugaenyang.gateway.security;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class TokenBlacklistSubscriber {

    private static final String CHANNEL = "token-blacklist";

    private final TokenBlacklistCache blacklistCache;
    private final ReactiveRedisMessageListenerContainer listenerContainer;

    public TokenBlacklistSubscriber(TokenBlacklistCache blacklistCache, ReactiveRedisConnectionFactory connectionFactory) {
        this.blacklistCache = blacklistCache;
        this.listenerContainer = new ReactiveRedisMessageListenerContainer(connectionFactory);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void subscribe() {
        listenerContainer.receive(ChannelTopic.of(CHANNEL))
            .doOnNext(message -> handle(message.getMessage()))
            .subscribe();
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
