package com.golajugaenyang.product.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productServiceOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Product Service API")
                .description("상품 도메인 API 문서")
                .version("v1"));
    }
}
