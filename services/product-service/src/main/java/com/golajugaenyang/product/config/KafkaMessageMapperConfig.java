package com.golajugaenyang.product.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class KafkaMessageMapperConfig {

    @Bean
    public tools.jackson.databind.ObjectMapper kafkaMessageObjectMapper() {
        return JsonMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .findAndAddModules()
            .build();
    }
}
