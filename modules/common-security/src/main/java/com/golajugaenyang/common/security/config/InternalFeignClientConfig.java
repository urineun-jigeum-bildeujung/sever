package com.golajugaenyang.common.security.config;

import com.golajugaenyang.common.security.filter.InternalSecretFeignInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class InternalFeignClientConfig {

    @Bean
    public InternalSecretFeignInterceptor internalSecretFeignInterceptor(
        @Value("${internal.gateway-secret}") String internalGatewaySecret
    ) {
        return new InternalSecretFeignInterceptor(internalGatewaySecret);
    }
}
