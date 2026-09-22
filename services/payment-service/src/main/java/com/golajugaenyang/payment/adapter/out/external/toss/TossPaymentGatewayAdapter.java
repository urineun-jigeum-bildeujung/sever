package com.golajugaenyang.payment.adapter.out.external.toss;


import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.payment.adapter.out.external.toss.client.TossPaymentApiClient;
import com.golajugaenyang.payment.adapter.out.external.toss.dto.TossCancelRequest;
import com.golajugaenyang.payment.adapter.out.external.toss.dto.TossConfirmRequest;
import com.golajugaenyang.payment.adapter.out.external.toss.dto.TossConfirmResponse;
import com.golajugaenyang.payment.application.payment.port.out.TossPaymentGatewayPort;
import com.golajugaenyang.payment.application.payment.port.out.dto.TossConfirmResult;
import com.golajugaenyang.payment.error.PaymentErrorCode;
import io.micrometer.core.instrument.MeterRegistry;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentGatewayAdapter implements TossPaymentGatewayPort {

    private final TossPaymentApiClient tossPaymentApiClient;
    private final MeterRegistry meterRegistry;

    @Override
    public TossConfirmResult confirm(String paymentKey, String orderId, BigDecimal amount) {
        try {
            TossConfirmResponse response = tossPaymentApiClient
                .confirm(new TossConfirmRequest(paymentKey, orderId, amount));
            return new TossConfirmResult(
                response.paymentKey(), response.orderId(),
                response.totalAmount(), response.method(),
                response.status(), response.approvedAt());
        } catch (HttpClientErrorException e) {
            log.warn("[TossConfirm] 승인 실패. paymentKey={}, response={}", paymentKey,
                e.getResponseBodyAsString());
            throw new AppException(PaymentErrorCode.TOSS_CONFIRM_FAILED);
        } catch (RestClientException e) {
            throw new AppException(PaymentErrorCode.TOSS_SERVICE_UNAVAILABLE);
        }
    }

    @Override
    public void cancel(String paymentKey, String reason, String idempotencyKey) {
        try {
            tossPaymentApiClient.cancel(paymentKey, idempotencyKey, new TossCancelRequest(reason));
        } catch (RestClientException e) {
            log.error("[TossCancel] 자동 취소 실패, 수동 확인 필요. paymentKey={}, reason={}",
                paymentKey, reason, e);
            meterRegistry.counter("payment.toss.cancel.failed").increment();
            throw new AppException(PaymentErrorCode.TOSS_CANCEL_FAILED);
        }
    }
}
