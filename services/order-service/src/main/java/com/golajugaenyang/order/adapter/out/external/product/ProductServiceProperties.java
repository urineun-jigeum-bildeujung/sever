package com.golajugaenyang.order.adapter.out.external.product;

import java.net.URI;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "product-service")
public record ProductServiceProperties(
    String baseUrl,
    Duration connectTimeout,
    Duration readTimeout
) {

    public ProductServiceProperties {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException(
                "product-service.base-url must be configured as an absolute HTTP(S) URL");
        }

        URI uri;
        try {
            uri = URI.create(baseUrl);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                "product-service.base-url must be configured as an absolute HTTP(S) URL",
                exception);
        }

        String scheme = uri.getScheme();
        if (uri.getHost() == null
            || !("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))) {
            throw new IllegalArgumentException(
                "product-service.base-url must be configured as an absolute HTTP(S) URL");
        }

        if (connectTimeout == null) {
            connectTimeout = Duration.ofSeconds(1);
        }
        if (readTimeout == null) {
            readTimeout = Duration.ofSeconds(2);
        }
    }
}
