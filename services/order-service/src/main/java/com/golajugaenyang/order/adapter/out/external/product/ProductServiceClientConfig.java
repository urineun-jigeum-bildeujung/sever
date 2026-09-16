package com.golajugaenyang.order.adapter.out.external.product;

import com.golajugaenyang.order.adapter.out.external.product.client.ProductInternalApiClient;
import com.golajugaenyang.order.adapter.out.external.product.client.TimeDealInternalApiClient;
import java.net.http.HttpClient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
    public RestClient productServiceRestClient(ProductServiceProperties properties) {
        HttpClient jdkHttpClient = HttpClient.newBuilder()
            .connectTimeout(properties.connectTimeout())
            .build();
        JdkClientHttpRequestFactory requestFactory =
            new JdkClientHttpRequestFactory(jdkHttpClient);
        requestFactory.setReadTimeout(properties.readTimeout());

        return RestClient.builder()
            .baseUrl(properties.baseUrl())
            .requestFactory(requestFactory)
            .build();
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

    @Bean(destroyMethod = "close")
    public ExecutorService cartCatalogExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
