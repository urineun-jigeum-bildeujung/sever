package com.golajugaenyang.payment.adapter.in.web.payment;


import com.golajugaenyang.common.security.annotation.MemberId;
import com.golajugaenyang.payment.adapter.in.web.payment.dto.ConfirmPaymentRequest;
import com.golajugaenyang.payment.adapter.in.web.payment.dto.ConfirmPaymentResponse;
import com.golajugaenyang.payment.adapter.in.web.payment.dto.RequestPaymentRequest;
import com.golajugaenyang.payment.adapter.in.web.payment.dto.RequestPaymentResponse;
import com.golajugaenyang.payment.application.payment.port.in.ConfirmPaymentUseCase;
import com.golajugaenyang.payment.application.payment.port.in.RequestPaymentUseCase;
import com.golajugaenyang.payment.application.payment.port.in.dto.ConfirmPaymentResult;
import com.golajugaenyang.payment.application.payment.port.in.dto.RequestPaymentResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController implements PaymentControllerDocs {

    private final RequestPaymentUseCase requestPaymentUseCase;
    private final ConfirmPaymentUseCase confirmPaymentUseCase;

    @Override
    @PostMapping
    public ResponseEntity<RequestPaymentResponse> requestPayment(
        @MemberId Long memberId,
        @Valid @RequestBody RequestPaymentRequest request
    ) {
        RequestPaymentResult result =
            requestPaymentUseCase.requestPayment(request.toCommand(memberId));
        return ResponseEntity.ok(RequestPaymentResponse.from(result));
    }

    @Override
    @PostMapping("/confirm")
    public ResponseEntity<ConfirmPaymentResponse> confirmPayment(
        @MemberId Long memberId,
        @Valid @RequestBody ConfirmPaymentRequest request
    ) {
        ConfirmPaymentResult result =
            confirmPaymentUseCase.confirmPayment(request.toCommand(memberId));
        return ResponseEntity.ok(ConfirmPaymentResponse.from(result));
    }
}
