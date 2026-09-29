package com.golajugaenyang.order.adapter.out.external.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class ProductServicePropertiesTest {

    @Test
    void appliesDefaultTimeoutsToValidLocalUrl() {
        ProductServiceProperties properties = new ProductServiceProperties(
            "http://localhost:8083", null, null);

        assertThat(properties.connectTimeout()).isEqualTo(Duration.ofSeconds(1));
        assertThat(properties.readTimeout()).isEqualTo(Duration.ofSeconds(2));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "/internal/products", "localhost:8083", "file:///tmp"})
    void rejectsMissingOrNonHttpAbsoluteUrl(String baseUrl) {
        assertThatThrownBy(() -> new ProductServiceProperties(baseUrl, null, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("product-service.base-url");
    }
}
