package com.golajugaenyang.order.adapter.in.web.order.dto;

import com.golajugaenyang.order.application.order.port.in.dto.CreateOrderCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateOrderRequest(
    @NotNull Long addressId,
    @NotNull Long petId,
    @NotEmpty @Valid List<Item> items,
    @Size(max = 200) String deliveryNote
) {

    public record Item(
        Long productId,
        Long dealItemId,
        @Positive int quantity
    ) {

        @AssertTrue(message = "productId와 dealItemId 중 하나만 존재해야 합니다.")
        public boolean isValidReference() {
            return (productId == null) != (dealItemId == null);
        }
    }

    public CreateOrderCommand toCommand(Long memberId, String idempotencyKey) {
        List<CreateOrderCommand.Item> commandItems = items.stream()
            .map(i -> new CreateOrderCommand.Item(
                i.productId(), i.dealItemId(), i.quantity())
            )
            .toList();
        return new CreateOrderCommand(
            memberId,
            petId,
            idempotencyKey,
            addressId,
            commandItems,
            deliveryNote
        );
    }
}
