package com.golajugaenyang.payment.adapter.in.web.payment;


import com.golajugaenyang.common.security.annotation.MemberId;
import com.golajugaenyang.payment.adapter.in.web.payment.dto.ConfirmPaymentRequest;
import com.golajugaenyang.payment.adapter.in.web.payment.dto.ConfirmPaymentResponse;
import com.golajugaenyang.payment.adapter.in.web.payment.dto.RequestPaymentRequest;
import com.golajugaenyang.payment.adapter.in.web.payment.dto.RequestPaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Payment", description = "결제 관련 API")
public interface PaymentControllerDocs {

    @Operation(
        summary = "결제 요청",
        description = """
            토스 결제위젯 렌더링에 필요한 정보(주문번호, 금액, 상품명, 구매자 식별값)를 반환합니다.
            금액은 요청에서 받지 않고 order-service에서 조회한 값을 그대로 사용합니다.
            같은 orderId로 재요청하면 기존 요청을 그대로 반환합니다.
            결제 실패 후 재시도는 현재 지원하지 않으며, 새 주문 생성이 필요합니다.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "결제 요청 정보 반환",
            content = @Content(schema = @Schema(implementation = RequestPaymentResponse.class))),
        @ApiResponse(responseCode = "403", description = "본인의 주문이 아님",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
        @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
        @ApiResponse(responseCode = "409", description = "결제 가능한 상태(PENDING)의 주문이 아님",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
        @ApiResponse(responseCode = "503", description = "order-service 일시 장애",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE))
    })
    ResponseEntity<RequestPaymentResponse> requestPayment(
        @MemberId Long memberId,
        @Valid @RequestBody RequestPaymentRequest request
    );

    @Operation(
        summary = "결제 승인",
        description = """
            토스 결제창이 successUrl로 리다이렉트하며 내려준 paymentKey/orderId/amount를
            그대로 전달해 결제를 최종 승인합니다.
            이미 승인 완료된 결제에 대한 재요청은 멱등하게 기존 결과를 그대로 반환합니다.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "결제 승인 성공 또는 이미 승인된 결제의 재현",
            content = @Content(schema = @Schema(implementation = ConfirmPaymentResponse.class))),
        @ApiResponse(responseCode = "403", description = "본인의 결제가 아님",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
        @ApiResponse(responseCode = "404", description = "결제 요청 정보를 찾을 수 없음",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
        @ApiResponse(responseCode = "409", description = "승인 불가 상태(이미 FAILED로 확정됨 등) 또는 금액 불일치",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
        @ApiResponse(responseCode = "500", description = "금액 불일치로 인한 자동 취소 실패 — 수동 확인 필요",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
        @ApiResponse(responseCode = "502", description = "토스 결제 승인 거부",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
        @ApiResponse(responseCode = "503", description = "토스 또는 order-service 일시 장애",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE))
    })
    ResponseEntity<ConfirmPaymentResponse> confirmPayment(
        @MemberId Long memberId,
        @Valid @RequestBody ConfirmPaymentRequest request
    );
}
