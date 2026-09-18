package com.golajugaenyang.payment.adapter.out.external.toss.dto;

public record TossCancelResponse(
    String paymentKey,
    String status
) {

}
