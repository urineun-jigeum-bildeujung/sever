package com.golajugaenyang.payment.adapter.out.external.toss.client;

import com.golajugaenyang.payment.adapter.out.external.toss.dto.TossCancelRequest;
import com.golajugaenyang.payment.adapter.out.external.toss.dto.TossCancelResponse;
import com.golajugaenyang.payment.adapter.out.external.toss.dto.TossConfirmRequest;
import com.golajugaenyang.payment.adapter.out.external.toss.dto.TossConfirmResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.PostExchange;

public interface TossPaymentApiClient {

    @PostExchange("/v1/payments/confirm")
    TossConfirmResponse confirm(@RequestBody TossConfirmRequest request);

    @PostExchange("/v1/payments/{paymentKey}/cancel")
    TossCancelResponse cancel(
        @PathVariable("paymentKey") String paymentKey,
        @RequestHeader("Idempotency-Key") String idempotencyKey,
        @RequestBody TossCancelRequest request);
}
