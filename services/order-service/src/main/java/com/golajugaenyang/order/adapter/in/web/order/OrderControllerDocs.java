package com.golajugaenyang.order.adapter.in.web.order;


import com.golajugaenyang.common.security.annotation.MemberId;
import com.golajugaenyang.order.adapter.in.web.order.dto.CreateOrderRequest;
import com.golajugaenyang.order.adapter.in.web.order.dto.CreateOrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@Tag(name = "Order", description = "주문 관련 API")
public interface OrderControllerDocs {

    @Operation(
        summary = "주문 생성",
        description = """
            상품/타임딜 조회 및 재고 예약을 거쳐 결제 대기(PENDING) 상태의 주문을 생성합니다.
            동일한 Idempotency-Key로 재요청하면 새 주문을 만들지 않고 기존 주문을 그대로 반환합니다
            (이 경우 응답 코드는 201이 아닌 200입니다).
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "신규 주문 생성 성공",
            content = @Content(schema = @Schema(implementation = CreateOrderResponse.class))),
        @ApiResponse(responseCode = "200", description = "동일 Idempotency-Key로 기존 주문 재반환",
            content = @Content(schema = @Schema(implementation = CreateOrderResponse.class))),
        @ApiResponse(responseCode = "404", description = "요청한 상품을 찾을 수 없음",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "구매 불가 상품 또는 재고 부족",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "503", description = "상품/재고 서비스 일시 장애",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    ResponseEntity<CreateOrderResponse> createOrder(
        @MemberId Long memberId,
        @Parameter(description = "클라이언트가 생성하는 재시도 안전용 멱등키 (UUID 권장)", required = true)
        @RequestHeader("Idempotency-Key") String idempotencyKey,
        @Valid @RequestBody CreateOrderRequest request
    );
}
