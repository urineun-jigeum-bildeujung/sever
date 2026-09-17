package com.golajugaenyang.payment.config;

import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class InternalGatewaySecretInterceptor implements ClientHttpRequestInterceptor {

    private static final String HEADER_NAME = "X-Internal-Secret";
    private final String secret;

    public InternalGatewaySecretInterceptor(String secret) {
        this.secret = secret;
    }

    @Override
    public ClientHttpResponse intercept(
        HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
        request.getHeaders().add(HEADER_NAME, secret);
        return execution.execute(request, body);
    }
}
