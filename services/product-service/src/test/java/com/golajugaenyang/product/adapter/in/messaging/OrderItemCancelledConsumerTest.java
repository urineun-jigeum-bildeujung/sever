package com.golajugaenyang.product.adapter.in.messaging;


import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.golajugaenyang.product.adapter.in.messaging.support.KafkaMessageMapper;
import com.golajugaenyang.product.application.inventory.port.in.InventoryCommandUseCase;
import com.golajugaenyang.product.config.KafkaMessageMapperConfig;
import com.golajugaenyang.product.domain.inventory.StockSubjectType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.exc.ValueInstantiationException;

@ExtendWith(MockitoExtension.class)
public class OrderItemCancelledConsumerTest {

    @Mock
    private InventoryCommandUseCase inventoryCommandUseCase;

    private final KafkaMessageMapper kafkaMessageMapper =
        new KafkaMessageMapper(new KafkaMessageMapperConfig().kafkaMessageObjectMapper());

    @Test
    @DisplayName("메시지를 받으면 release()를 호출한다.")
    void invokes_release_not_restore() {
        OrderItemCancelledConsumer consumer = new OrderItemCancelledConsumer(
            inventoryCommandUseCase, kafkaMessageMapper);
        String rawMessage = """
            {"orderId": 500, "orderItemId": 1, "subjectType": "PRODUCT", "subjectId": 100, "quantity": 2}
            """;

        consumer.onMessage(rawMessage);

        verify(inventoryCommandUseCase).release(StockSubjectType.PRODUCT, 100L, 1L, 2);
        verify(inventoryCommandUseCase, never()).restore(any(), any(), any(), anyInt());
    }

    @Test
    @DisplayName("이 서비스가 모르는 필드(orderId)가 있어도 역직렬화에 실패하지 않는다.")
    void ignores_unknown_field() {
        OrderItemCancelledConsumer consumer = new OrderItemCancelledConsumer(
            inventoryCommandUseCase, kafkaMessageMapper);
        String rawMessage = """
            {"orderId": 500, "orderItemId": 1, "subjectType": "PRODUCT", "subjectId": 100, "quantity": 2}
            """;

        assertThatCode(() -> consumer.onMessage(rawMessage)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("quantity가 0 이하인 메시지는 파싱 시점에 예외가 발생해 유스케이스가 호출되지 않는다.")
    void throws_when_quantity_is_not_positive() {
        OrderItemCancelledConsumer consumer = new OrderItemCancelledConsumer(
            inventoryCommandUseCase, kafkaMessageMapper);
        String rawMessage = """
            {"orderId": 500, "orderItemId": 1, "subjectType": "PRODUCT", "subjectId": 100, "quantity": 0}
            """;

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> consumer.onMessage(rawMessage))
            .isInstanceOf(ValueInstantiationException.class);
    }
}
