package com.golajugaenyang.product.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "time-deal.list")
public record TimeDealListProperties(
    double lowStockThresholdRatio
) {

    public TimeDealListProperties {
        if (Double.isNaN(lowStockThresholdRatio)
            || lowStockThresholdRatio < 0
            || lowStockThresholdRatio > 1
        ) {
            throw new IllegalStateException(
                "time-deal.list.low-stock-threshold-ratio는 0~1 사이여야 합니다. "
                    + "현재 값=" + lowStockThresholdRatio);
        }
    }
}
