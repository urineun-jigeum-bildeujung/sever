package com.golajugaenyang.payment.config;

import com.golajugaenyang.payment.adapter.out.external.order.client.OrderInternalApiClient;
import com.golajugaenyang.payment.adapter.out.external.toss.client.TossPaymentApiClient;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ExternalClientConfig {

    @Bean
    public RestClient orderServiceRestClient(
        OrderServiceProperties properties,
        @Value("${internal.gateway-secret}") String internalGatewaySecret
    ) {
        HttpClient jdkHttpClient = HttpClient.newBuilder()
            .connectTimeout(properties.connectTimeout()).build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(jdkHttpClient);
        requestFactory.setReadTimeout(properties.readTimeout());
        return RestClient.builder()
            .baseUrl(properties.baseUrl())
            .requestFactory(requestFactory)
            .requestInterceptor(new InternalGatewaySecretInterceptor(internalGatewaySecret))
            .build();
    }

    @Bean
    public OrderInternalApiClient orderInternalApiClient(RestClient orderServiceRestClient) {
        return HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(orderServiceRestClient))
            .build().createClient(OrderInternalApiClient.class);
    }

    @Bean
    public RestClient tossPaymentRestClient(TossPaymentProperties properties) {
        String credential = Base64.getEncoder()
            .encodeToString((properties.secretKey() + ":").getBytes(StandardCharsets.UTF_8));
        HttpClient jdkHttpClient = HttpClient.newBuilder()
            .connectTimeout(properties.connectTimeout())
            .build();
        JdkClientHttpRequestFactory requestFactory =
            new JdkClientHttpRequestFactory(jdkHttpClient);
        requestFactory.setReadTimeout(properties.readTimeout());
        return RestClient.builder()
            .baseUrl(properties.baseUrl())
            .requestFactory(requestFactory)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + credential)
            .build();
    }

    @Bean
    public TossPaymentApiClient tossPaymentApiClient(RestClient tossPaymentRestClient) {
        return HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(tossPaymentRestClient))
            .build().createClient(TossPaymentApiClient.class);
    }
}
