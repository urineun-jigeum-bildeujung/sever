package com.golajugaenyang.order.adapter.out.external.product;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "product-service")
public record ProductServiceProperties(
    String baseUrl,
    Duration connectTimeout,
    Duration readTimeout
) {

    public ProductServiceProperties {
        if (connectTimeout == null) {
            connectTimeout = Duration.ofSeconds(1);
        }
        if (readTimeout == null) {
            readTimeout = Duration.ofSeconds(2);
        }
    }
}
