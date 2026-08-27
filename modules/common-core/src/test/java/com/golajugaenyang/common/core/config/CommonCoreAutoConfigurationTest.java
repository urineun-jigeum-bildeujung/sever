package com.golajugaenyang.common.core.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.golajugaenyang.common.core.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;


class CommonCoreAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(CommonCoreAutoConfiguration.class));

    @Test
    @DisplayName("GlobalExceptionHandler가 자동 등록된다.")
    void globalExceptionHandler_shouldBeAutoRegistered() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(GlobalExceptionHandler.class);
        });
    }
}
