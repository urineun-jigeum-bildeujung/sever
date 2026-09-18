package com.golajugaenyang.payment.adapter.in.web.payment.dto;

import com.golajugaenyang.payment.application.payment.port.in.dto.RequestPaymentCommand;
import jakarta.validation.constraints.NotNull;

public record RequestPaymentRequest(
    @NotNull Long orderId
) {

    public RequestPaymentCommand toCommand(Long memberId) {
        return new RequestPaymentCommand(orderId, memberId);
    }
}
