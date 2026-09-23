package com.golajugaenyang.order.adapter.in.web.claim.dto;

import com.golajugaenyang.order.application.claim.port.in.dto.CreateClaimCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateClaimRequest(
    @NotBlank String claimType,
    @NotBlank String reasonCode,
    @Size(max = 1000) String reason,
    @NotEmpty @Valid List<Item> items,
    List<String> imageUrls
) {

    public record Item(
        @NotNull Long orderItemId,
        @Positive int quantity
    ) {

    }

    @AssertTrue(message = "동일한 주문 품목에 대해 중복된 신청 항목을 포함할 수 없습니다.")
    public boolean isItemsDistinct() {
        if (items == null) {
            return true;
        }
        long distinctCount = items.stream()
            .map(Item::orderItemId)
            .distinct()
            .count();
        return distinctCount == items.size();
    }

    public CreateClaimCommand toCommand(Long orderId, Long memberId) {
        List<CreateClaimCommand.Item> commandItems = items.stream()
            .map(i -> new CreateClaimCommand.Item(i.orderItemId(), i.quantity()))
            .toList();
        return new CreateClaimCommand(
            orderId, memberId, claimType, reasonCode, reason, commandItems, imageUrls);
    }
}
