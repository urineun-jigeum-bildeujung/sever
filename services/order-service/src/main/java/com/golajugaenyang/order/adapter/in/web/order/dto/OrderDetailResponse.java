package com.golajugaenyang.order.adapter.in.web.order.dto;

import com.golajugaenyang.order.application.order.port.in.dto.OrderDetailResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderDetailResponse(
    Long orderId,
    String orderNumber,
    String orderStatus,
    OffsetDateTime deliveredAt,
    BigDecimal productAmount,
    BigDecimal totalAmount,
    List<ItemDetail> items,
    DeliveryAddressDetail deliveryAddress,
    String deliveryNote,
    PaymentSummary payment
) {

    public record ItemDetail(
        Long orderItemId,
        String thumbnailUrl,
        String productName,
        int quantity,
        int cancelledQuantity,
        int returnedQuantity,
        int effectiveQuantity,
        @Schema(description = "할인 전 단가")
        BigDecimal unitPrice,
        @Schema(description = "할인이 반영된 실제 결제 단가")
        BigDecimal paidUnitPrice,
        @Schema(description = "주문 시점 기준 해당 품목 실제 결제 금액 (paidUnitPrice × quantity)")
        BigDecimal amount,
        String itemStatus,
        List<ClaimSummary> claims
    ) {

    }

    public record ClaimSummary(
        Long claimId,
        String claimType,
        String claimStatus,
        Instant requestedAt,
        Instant completedAt
    ) {

    }

    public record DeliveryAddressDetail(
        String receiver,
        String receiverPhone,
        String zipCode,
        String address,
        String addressDetail
    ) {

    }

    public record PaymentSummary(
        OffsetDateTime paidAt,
        String method
    ) {

    }

    public static OrderDetailResponse from(OrderDetailResult result) {
        List<ItemDetail> items = result.items().stream()
            .map(i -> new ItemDetail(
                i.orderItemId(), i.thumbnailUrl(), i.productName(),
                i.quantity(), i.cancelledQuantity(),
                i.returnedQuantity(), i.effectiveQuantity(),
                i.unitPrice(), i.paidUnitPrice(), i.amount(), i.itemStatus(),
                i.claims().stream()
                    .map(c -> new ClaimSummary(
                        c.claimId(), c.claimType(), c.claimStatus(),
                        c.requestedAt(), c.completedAt()))
                    .toList()))
            .toList();

        DeliveryAddressDetail address = new DeliveryAddressDetail(
            result.deliveryAddress().receiver(),
            result.deliveryAddress().receiverPhone(),
            result.deliveryAddress().zipCode(),
            result.deliveryAddress().address(),
            result.deliveryAddress().addressDetail());

        PaymentSummary payment = result.payment() != null
            ? new PaymentSummary(
            result.payment().paidAt(), result.payment().method())
            : null;

        return new OrderDetailResponse(
            result.orderId(), result.orderNumber(),
            result.orderStatus(), result.deliveredAt(),
            result.productAmount(), result.totalAmount(),
            items, address, result.deliveryNote(), payment);
    }
}
