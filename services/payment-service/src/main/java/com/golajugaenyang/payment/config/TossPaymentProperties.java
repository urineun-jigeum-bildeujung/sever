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
        if (baseUrl == null || !baseUrl.startsWith("https://")) {
            throw new IllegalStateException(
                "toss-payments.base-url must use https://. 현재 값: " + baseUrl);
        }
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("toss-payments.secret-key must not be blank");
        }
        if (connectTimeout == null) {
            connectTimeout = Duration.ofSeconds(2);
        }
        if (readTimeout == null) {
            readTimeout = Duration.ofSeconds(5);
        }
    }
}
