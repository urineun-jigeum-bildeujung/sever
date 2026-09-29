package com.golajugaenyang.payment.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

class OrderServicePropertiesTest {

    @Test
    void appliesDefaultTimeoutsToValidLocalUrl() {
        OrderServiceProperties properties = new OrderServiceProperties(
            "http://localhost:8084", null, null);

        assertThat(properties.connectTimeout()).isEqualTo(Duration.ofSeconds(1));
        assertThat(properties.readTimeout()).isEqualTo(Duration.ofSeconds(2));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "/internal/orders", "localhost:8084", "file:///tmp"})
    void rejectsMissingOrNonHttpAbsoluteUrl(String baseUrl) {
        assertThatThrownBy(() -> new OrderServiceProperties(baseUrl, null, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("order-service.base-url");
    }

    @Test
    void validatesUrlDuringConfigurationPropertiesBinding() {
        Binder binder = new Binder(new MapConfigurationPropertySource(Map.of(
            "order-service.base-url", "https:///missing-host"
        )));

        assertThatThrownBy(() -> binder.bind(
            "order-service", Bindable.of(OrderServiceProperties.class)))
            .hasRootCauseInstanceOf(IllegalArgumentException.class)
            .hasStackTraceContaining("order-service.base-url");
    }
}
