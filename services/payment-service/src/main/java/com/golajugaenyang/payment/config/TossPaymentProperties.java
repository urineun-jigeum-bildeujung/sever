package com.golajugaenyang.payment.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "toss-payments")
public record TossPaymentProperties(
    String baseUrl,
    String secretKey,
    Duration connectTimeout,
    Duration readTimeout
) {

    public TossPaymentProperties {
        if (connectTimeout == null) {
            connectTimeout = Duration.ofSeconds(2);
        }
        if (readTimeout == null) {
            readTimeout = Duration.ofSeconds(5);
        }
    }
}
