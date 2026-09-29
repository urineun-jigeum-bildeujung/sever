package com.golajugaenyang.order.config;

import java.net.URI;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "member-service")
public record MemberServiceProperties(
    String baseUrl,
    Duration connectTimeout,
    Duration readTimeout
) {

    public MemberServiceProperties {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException(
                "member-service.base-url must be configured as an absolute HTTP(S) URL");
        }

        URI uri;
        try {
            uri = URI.create(baseUrl);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                "member-service.base-url must be configured as an absolute HTTP(S) URL",
                exception);
        }

        String scheme = uri.getScheme();
        if (uri.getHost() == null
            || !("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))) {
            throw new IllegalArgumentException(
                "member-service.base-url must be configured as an absolute HTTP(S) URL");
        }

        if (connectTimeout == null) {
            connectTimeout = Duration.ofSeconds(1);
        }
        if (readTimeout == null) {
            readTimeout = Duration.ofSeconds(2);
        }
    }
}
