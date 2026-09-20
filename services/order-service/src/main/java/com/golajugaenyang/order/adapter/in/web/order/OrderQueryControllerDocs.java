package com.golajugaenyang.order.adapter.in.web.order;


import com.golajugaenyang.common.security.annotation.MemberId;
import com.golajugaenyang.order.adapter.in.web.order.dto.OrderDetailResponse;
import com.golajugaenyang.order.adapter.in.web.order.dto.OrderListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Order", description = "주문 조회 관련 API")
public interface OrderQueryControllerDocs {

    @Operation(
        summary = "주문 목록 조회 (커서 기반 무한스크롤)",
        description = "주문일자 내림차순으로 조회하며, 각 주문에 포함된 품목도 함께 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = OrderListResponse.class))),
        @ApiResponse(responseCode = "400", description = "유효하지 않은 커서",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE))
    })
    ResponseEntity<OrderListResponse> getOrderList(
        @MemberId Long memberId,
        @Parameter(description = "이전 응답의 nextCursor 값. 첫 페이지는 생략") @RequestParam(required = false) String cursor,
        @Parameter(description = "페이지당 개수, 최대 50") @RequestParam(defaultValue = "20") @Max(50) @Positive int size
    );

    @Operation(
        summary = "주문 상세 조회",
        description = """
            주문/품목 상태, 배송지, 결제 정보, 품목별 반품·교환 신청 현황을 조회합니다.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = OrderDetailResponse.class))),
        @ApiResponse(responseCode = "403", description = "본인의 주문이 아님",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
        @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE))
    })
    ResponseEntity<OrderDetailResponse> getOrderDetail(
        @MemberId Long memberId,
        @PathVariable Long orderId);
}
