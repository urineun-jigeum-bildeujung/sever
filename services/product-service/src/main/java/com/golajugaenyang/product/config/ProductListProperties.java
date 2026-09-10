package com.golajugaenyang.product.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "product.list")
public record ProductListProperties(
    int defaultSize,
    int maxSize
) {

    public ProductListProperties {
        if (defaultSize <= 0 || maxSize <= 0 || defaultSize > maxSize) {
            throw new IllegalStateException(
                "product.list.default-size(%d)와 product.list.max-size(%d) 설정이 유효하지 않습니다."
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
