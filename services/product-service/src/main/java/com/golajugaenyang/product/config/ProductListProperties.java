package com.golajugaenyang.product.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "product.list")
public record ProductListProperties(
    int defaultSize,
    int maxSize
) {

    public ProductListProperties {
        if (defaultSize > maxSize) {
            throw new IllegalStateException(
                "product.list.default-size(%d)는 product.list.max-size(%d)보다 클 수 없습니다."
                    .formatted(defaultSize, maxSize));
        }
    }

    public int resolveSize(Integer requestedSize) {
        if (requestedSize == null) {
            return defaultSize;
        }
        return Math.min(requestedSize, maxSize);
    }
}
