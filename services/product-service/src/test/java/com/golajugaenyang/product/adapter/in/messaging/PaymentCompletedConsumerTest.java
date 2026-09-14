package com.golajugaenyang.product.adapter.in.messaging;


import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.golajugaenyang.product.application.inventory.port.in.InventoryCommandUseCase;
import com.golajugaenyang.product.domain.inventory.StockSubjectType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;


@ExtendWith(MockitoExtension.class)
public class PaymentCompletedConsumerTest {

    @Mock
    private InventoryCommandUseCase inventoryCommandUseCase;

    @InjectMocks
    private PaymentCompletedConsumer consumer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("JSON 메시지를 정상적으로 파싱해 올바른 인자로 호출한다.")
    void parses_message_and_invokes_confirm_with_correct_arguments() {
        PaymentCompletedConsumer consumer = new PaymentCompletedConsumer(
            inventoryCommandUseCase, objectMapper);
        String rawMessage = """
            {"orderItemId": 1, "subjectType": "PRODUCT", "subjectId": 100, "quantity": 2}
            """;

        consumer.onMessage(rawMessage);

        verify(inventoryCommandUseCase).confirm(
            eq(StockSubjectType.PRODUCT), eq(100L), eq(1L), eq(2));
    }
}
