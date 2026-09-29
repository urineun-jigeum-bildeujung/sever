package com.golajugaenyang.order.config;


import com.golajugaenyang.order.adapter.out.external.common.InternalGatewaySecretInterceptor;
import com.golajugaenyang.order.adapter.out.external.member.client.MemberInternalApiClient;
import java.net.URI;
import java.net.http.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class MemberServiceClientConfig {

    @Bean
    public RestClient memberServiceRestClient(
        MemberServiceProperties properties,
        @Value("${internal.gateway-secret}") String internalGatewaySecret,
        @Value("${internal.mtls.enabled:false}") boolean internalMtlsEnabled,
        SslBundles sslBundles
    ) {
        HttpClient jdkHttpClient = createHttpClient(
            properties, internalMtlsEnabled, sslBundles);
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(jdkHttpClient);
        requestFactory.setReadTimeout(properties.readTimeout());

        return RestClient.builder()
            .baseUrl(properties.baseUrl())
            .requestFactory(requestFactory)
            .requestInterceptor(new InternalGatewaySecretInterceptor(internalGatewaySecret))
            .build();
    }

    HttpClient createHttpClient(
        MemberServiceProperties properties,
        boolean internalMtlsEnabled,
        SslBundles sslBundles
    ) {
        HttpClient.Builder clientBuilder = HttpClient.newBuilder()
            .connectTimeout(properties.connectTimeout());

        if (internalMtlsEnabled) {
            String scheme = URI.create(properties.baseUrl()).getScheme();
            if (!"https".equalsIgnoreCase(scheme)) {
                throw new IllegalArgumentException(
                    "member-service.base-url must use HTTPS when internal mTLS is enabled");
            }
            clientBuilder.sslContext(
                sslBundles.getBundle("internalmtls").createSslContext());
        }

        return clientBuilder.build();
    }

    @Bean
    public MemberInternalApiClient memberInternalApiClient(RestClient memberServiceRestClient) {
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(memberServiceRestClient))
            .build().createClient(MemberInternalApiClient.class);
    }
}
