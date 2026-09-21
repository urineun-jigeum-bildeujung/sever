package com.golajugaenyang.order.config;


import com.golajugaenyang.order.adapter.out.external.common.InternalGatewaySecretInterceptor;
import com.golajugaenyang.order.adapter.out.external.member.client.MemberInternalApiClient;
import java.net.http.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class MemberServiceClientConfig {

    @Bean
    public RestClient memberServiceRestClient(
        MemberServiceProperties properties,
        @Value("${internal.gateway-secret}") String internalGatewaySecret
    ) {

        HttpClient jdkHttpClient = HttpClient.newBuilder().connectTimeout(properties.connectTimeout()).build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(jdkHttpClient);
        requestFactory.setReadTimeout(properties.readTimeout());

        return RestClient.builder()
            .baseUrl(properties.baseUrl())
            .requestFactory(requestFactory)
            .requestInterceptor(new InternalGatewaySecretInterceptor(internalGatewaySecret))
            .build();
    }

    @Bean
    public MemberInternalApiClient memberInternalApiClient(RestClient memberServiceRestClient) {
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(memberServiceRestClient))
            .build().createClient(MemberInternalApiClient.class);
    }
}
