package com.golajugaenyang.payment.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "order-service")
public record OrderServiceProperties(
    String baseUrl,
    Duration connectTimeout,
    Duration readTimeout
) {

    public OrderServiceProperties {
        if (connectTimeout == null) {
            connectTimeout = Duration.ofSeconds(1);
        }
        if (readTimeout == null) {
            readTimeout = Duration.ofSeconds(2);
        }
    }
}
