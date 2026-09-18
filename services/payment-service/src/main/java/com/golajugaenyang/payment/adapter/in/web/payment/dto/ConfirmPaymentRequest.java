package com.golajugaenyang.payment.adapter.in.web.payment.dto;

import com.golajugaenyang.payment.application.payment.port.in.dto.ConfirmPaymentCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ConfirmPaymentRequest(
    @NotBlank String paymentKey,
    @NotBlank String orderId,
    @NotNull BigDecimal amount
) {

    public ConfirmPaymentCommand toCommand(Long memberId) {
        return new ConfirmPaymentCommand(paymentKey, orderId, amount, memberId);
    }
}
