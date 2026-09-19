package com.golajugaenyang.order.adapter.in.web.claim.dto;

import com.golajugaenyang.order.application.claim.port.in.dto.CreateClaimCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateClaimRequest(
    @NotBlank String claimType,
    @Size(max = 1000) String reason,
    @NotEmpty @Valid List<Item> items,
    List<String> imageUrls
) {

    public record Item(
        @NotNull Long orderItemId,
        @Positive int quantity
    ) {

    }

    public CreateClaimCommand toCommand(Long orderId, Long memberId) {
        List<CreateClaimCommand.Item> commandItems = items.stream()
            .map(i -> new CreateClaimCommand.Item(i.orderItemId(), i.quantity()))
            .toList();
        return new CreateClaimCommand(
            orderId, memberId, claimType, reason, commandItems, imageUrls);
    }
}
