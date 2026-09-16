package com.golajugaenyang.order.config;


import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "order.reservation")
public record OrderReservationProperties(
    Duration ttl
) {

    public OrderReservationProperties {
        if (ttl == null) {
            ttl = Duration.ofMinutes(15);
        }
    }
}
