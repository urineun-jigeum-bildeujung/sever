package com.golajugaenyang.order.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "member-service")
public record MemberServiceProperties(
    String baseUrl,
    Duration connectTimeout,
    Duration readTimeout
) {

    public MemberServiceProperties {
        if (connectTimeout == null) {
            connectTimeout = Duration.ofSeconds(1);
        }
        if (readTimeout == null) {
            readTimeout = Duration.ofSeconds(2);
        }
    }
}
