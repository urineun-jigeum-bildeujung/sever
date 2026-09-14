package com.golajugaenyang.common.security.filter;

import feign.RequestInterceptor;
import feign.RequestTemplate;


public class InternalSecretFeignInterceptor implements RequestInterceptor {

    private final String internalGatewaySecret;

    public InternalSecretFeignInterceptor(String internalGatewaySecret) {
        this.internalGatewaySecret = internalGatewaySecret;
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header(InternalGatewaySecurityFilter.INTERNAL_SECRET_HEADER, internalGatewaySecret);
    }
}
