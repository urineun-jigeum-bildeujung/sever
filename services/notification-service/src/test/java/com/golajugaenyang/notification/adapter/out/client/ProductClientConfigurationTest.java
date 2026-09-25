package com.golajugaenyang.notification.adapter.out.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.golajugaenyang.common.security.config.InternalFeignClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClient;

class ProductClientConfigurationTest {

    @Test
    void usesInternalFeignClientConfigurationForMtlsAndGatewaySecret() {
        FeignClient annotation = ProductClient.class.getAnnotation(FeignClient.class);

        assertThat(annotation).isNotNull();
        assertThat(annotation.configuration()).containsExactly(InternalFeignClientConfig.class);
    }
}
