package com.golajugaenyang.order.adapter.out.external.product;

import com.golajugaenyang.order.adapter.out.external.common.InternalGatewaySecretInterceptor;
import com.golajugaenyang.order.adapter.out.external.inventory.client.InventoryInternalApiClient;
import com.golajugaenyang.order.adapter.out.external.product.client.ProductInternalApiClient;
import com.golajugaenyang.order.adapter.out.external.product.client.TimeDealInternalApiClient;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;


@Configuration
@EnableConfigurationProperties(ProductServiceProperties.class)
public class ProductServiceClientConfig {

    @Bean
    public RestClient productServiceRestClient(
        ProductServiceProperties properties,
        @Value("${internal.gateway-secret}") String internalGatewaySecret,
        @Value("${internal.mtls.enabled:false}") boolean internalMtlsEnabled,
        SslBundles sslBundles
    ) {
        HttpClient jdkHttpClient = createHttpClient(
            properties, internalMtlsEnabled, sslBundles);
        JdkClientHttpRequestFactory requestFactory =
            new JdkClientHttpRequestFactory(jdkHttpClient);
        requestFactory.setReadTimeout(properties.readTimeout());

        return RestClient.builder()
            .baseUrl(properties.baseUrl())
            .requestFactory(requestFactory)
            .requestInterceptor(new InternalGatewaySecretInterceptor(internalGatewaySecret))
            .build();
    }

    HttpClient createHttpClient(
        ProductServiceProperties properties,
        boolean internalMtlsEnabled,
        SslBundles sslBundles
    ) {
        HttpClient.Builder clientBuilder = HttpClient.newBuilder()
            .connectTimeout(properties.connectTimeout());

        if (internalMtlsEnabled) {
            String scheme = URI.create(properties.baseUrl()).getScheme();
            if (!"https".equalsIgnoreCase(scheme)) {
                throw new IllegalArgumentException(
                    "product-service.base-url must use HTTPS when internal mTLS is enabled");
            }
            clientBuilder.sslContext(
                sslBundles.getBundle("internalmtls").createSslContext());
        }

        return clientBuilder.build();
    }

    @Bean
    public ProductInternalApiClient productInternalApiClient(
        RestClient productServiceRestClient
    ) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(productServiceRestClient))
            .build();
        return factory.createClient(ProductInternalApiClient.class);
    }

    @Bean
    public TimeDealInternalApiClient timeDealInternalApiClient(
        RestClient productServiceRestClient
    ) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(productServiceRestClient))
            .build();
        return factory.createClient(TimeDealInternalApiClient.class);
    }

    @Bean
    public InventoryInternalApiClient inventoryInternalApiClient(
        RestClient productServiceRestClient
    ) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(productServiceRestClient))
            .build();
        return factory.createClient(InventoryInternalApiClient.class);
    }

    @Bean(destroyMethod = "close")
    public ExecutorService cartCatalogExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
