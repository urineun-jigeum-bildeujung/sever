package com.golajugaenyang.review.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.golajugaenyang.review.adapter.out.client.dto.ConfirmedItemsResponse;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.mock.http.MockHttpInputMessage;
import tools.jackson.databind.json.JsonMapper;

class ReviewFeignMessageConvertersConfigTest {

    private final ReviewFeignMessageConvertersConfig config = new ReviewFeignMessageConvertersConfig();

    @Test
    void registersJacksonConverterWhenFeignStartsWithNoDefaultConverters() throws Exception {
        JsonMapper jsonMapper = JsonMapper.builder().findAndAddModules().build();
        var customizer = config.reviewFeignMessageConvertersCustomizer(jsonMapper);
        var builder = HttpMessageConverters.forClient().disableDefaults();

        customizer.customize(builder);

        JacksonJsonHttpMessageConverter converter = (JacksonJsonHttpMessageConverter) StreamSupport
                .stream(builder.build().spliterator(), false)
                .filter(JacksonJsonHttpMessageConverter.class::isInstance)
                .findFirst()
                .orElseThrow();
        var input = new MockHttpInputMessage(("""
                {"items":[{"productId":1,"petId":2,"orderId":3,"orderItemId":4,
                "orderStatus":"CONFIRMED","itemStatus":"PAID",
                "confirmedAt":"2026-09-29T15:00:00Z"}]}
                """).getBytes(StandardCharsets.UTF_8));
        input.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ConfirmedItemsResponse response =
                (ConfirmedItemsResponse) converter.read(ConfirmedItemsResponse.class, input);

        assertThat(response.items()).hasSize(1);
        assertThat(response.items().getFirst().confirmedAt())
                .isEqualTo(OffsetDateTime.parse("2026-09-29T15:00:00Z"));
    }
}
