package com.golajugaenyang.order.adapter.in.web.order;


import com.golajugaenyang.common.security.annotation.MemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;


@Tag(name = "Order", description = "주문 상태 관련 API")
public interface OrderStatusControllerDocs {

    @Operation(summary = "구매 확정", description = """
        배송 완료된 주문을 구매 확정 처리합니다.
        이미 구매 확정된 주문에 대한 재요청은 멱등하게 성공 처리됩니다.
        """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "구매 확정 성공"),
        @ApiResponse(responseCode = "403", description = "본인의 주문이 아님", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
        @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
        @ApiResponse(responseCode = "409", description = "구매 확정 가능한 상태(DELIVERED)가 아님", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE))
    })
    ResponseEntity<Void> confirmOrder(@MemberId Long memberId, @PathVariable Long orderId);

    @Operation(
        summary = "주문 취소",
        description = """
            배송 시작 전(PENDING/PAID/PREPARING) 주문 전체를 취소합니다.
            이미 취소된 주문에 대한 재요청은 멱등하게 성공 처리됩니다.
            현재는 전체 취소만 지원하며 품목 단위 부분 취소는 지원하지 않습니다.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "취소 접수 성공"),
        @ApiResponse(responseCode = "403", description = "본인의 주문이 아님", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
        @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
        @ApiResponse(responseCode = "409", description = "취소 가능한 상태(배송 전)가 아님", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE))
    })
    ResponseEntity<Void> cancelOrder(@MemberId Long memberId, @PathVariable Long orderId);
}
