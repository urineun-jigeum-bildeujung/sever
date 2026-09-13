package com.golajugaenyang.product.adapter.in.internal.inventory.dto;

import com.golajugaenyang.product.application.inventory.port.in.dto.ReserveItemCommand;
import com.golajugaenyang.product.domain.inventory.StockSubjectType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ReserveBulkRequest(
    @NotEmpty @Size(max = 50) @Valid List<Item> items
) {

    public record Item(
        @NotNull Long orderItemId,
        @NotNull StockSubjectType subjectType,
        @NotNull Long subjectId,
        @Positive int quantity
    ) {

        public ReserveItemCommand toCommand() {
            return new ReserveItemCommand(orderItemId, subjectType, subjectId, quantity);
        }
    }
}
