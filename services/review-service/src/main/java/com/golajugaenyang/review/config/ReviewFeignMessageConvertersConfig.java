package com.golajugaenyang.review.config;

import org.springframework.boot.http.converter.autoconfigure.ClientHttpMessageConvertersCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import tools.jackson.databind.json.JsonMapper;

/**
 * Makes the Boot-configured Jackson converter available inside each Feign child context.
 *
 * <p>Without this explicit bridge, OpenFeign 5.0.2 can build an empty converter list in
 * the review service and fail a successful internal response with
 * {@code 'messageConverters' must not be empty}.</p>
 */
public class ReviewFeignMessageConvertersConfig {

    @Bean
    public ClientHttpMessageConvertersCustomizer reviewFeignMessageConvertersCustomizer(JsonMapper jsonMapper) {
        var jsonConverter = new JacksonJsonHttpMessageConverter(jsonMapper);
        return builder -> builder.withJsonConverter(jsonConverter);
    }
}
