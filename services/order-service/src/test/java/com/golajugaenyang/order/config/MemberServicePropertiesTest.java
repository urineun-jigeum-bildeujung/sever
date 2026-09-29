package com.golajugaenyang.order.config;

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

class MemberServicePropertiesTest {

    @Test
    void appliesDefaultTimeoutsToValidLocalUrl() {
        MemberServiceProperties properties = new MemberServiceProperties(
            "http://localhost:8082", null, null);

        assertThat(properties.connectTimeout()).isEqualTo(Duration.ofSeconds(1));
        assertThat(properties.readTimeout()).isEqualTo(Duration.ofSeconds(2));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "/internal/members", "localhost:8082", "file:///tmp"})
    void rejectsMissingOrNonHttpAbsoluteUrl(String baseUrl) {
        assertThatThrownBy(() -> new MemberServiceProperties(baseUrl, null, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("member-service.base-url");
    }

    @Test
    void validatesUrlDuringConfigurationPropertiesBinding() {
        Binder binder = new Binder(new MapConfigurationPropertySource(Map.of(
            "member-service.base-url", "/relative/path"
        )));

        assertThatThrownBy(() -> binder.bind(
            "member-service", Bindable.of(MemberServiceProperties.class)))
            .hasRootCauseInstanceOf(IllegalArgumentException.class)
            .hasStackTraceContaining("member-service.base-url");
    }
}
