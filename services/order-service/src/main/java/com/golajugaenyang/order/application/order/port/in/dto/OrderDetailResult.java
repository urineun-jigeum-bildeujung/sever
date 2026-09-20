package com.golajugaenyang.order.application.order.port.in.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderDetailResult(
    Long orderId,
    String orderNumber,
    String orderStatus,
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
        BigDecimal unitPrice,
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
}
