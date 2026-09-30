package com.golajugaenyang.product.adapter.in.messaging.support;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;


@Component
public class KafkaMessageMapper {

    private final ObjectMapper delegate;

    public KafkaMessageMapper(@Qualifier("kafkaMessageObjectMapper") ObjectMapper delegate) {
        this.delegate = delegate;
    }

    public <T> T readValue(String rawMessage, Class<T> type) {
        return delegate.readValue(rawMessage, type);
    }
}
