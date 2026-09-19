package com.golajugaenyang.order.adapter.in.web.claim;


import com.golajugaenyang.common.security.annotation.MemberId;
import com.golajugaenyang.order.adapter.in.web.claim.dto.CreateClaimRequest;
import com.golajugaenyang.order.adapter.in.web.claim.dto.CreateClaimResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Claim", description = "반품/교환 신청 관련 API")

public interface ClaimControllerDocs {

    @Operation(
        summary = "반품/교환 신청",
        description = """
            배송 완료 후 7일 이내(구매 확정 전)의 주문에 대해 반품 또는 교환을 신청합니다.
            사유는 선택 작성입니다. 이 단계에서는 재고/결제에 영향을 주지 않고 신청 접수만 생성됩니다.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "신청 접수 성공",
            content = @Content(schema = @Schema(implementation = CreateClaimResponse.class))),
        @ApiResponse(responseCode = "400", description = "유효하지 않은 신청 유형",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
        @ApiResponse(responseCode = "403", description = "본인의 주문이 아님",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
        @ApiResponse(responseCode = "404", description = "주문 또는 품목을 찾을 수 없음",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
        @ApiResponse(responseCode = "409", description = "신청 가능 기간이 아니거나, 이미 진행 중인 신청이 있거나, 수량 초과",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE))
    })
    ResponseEntity<CreateClaimResponse> createClaim(
        @MemberId Long memberId,
        @PathVariable Long orderId,
        @Valid @RequestBody CreateClaimRequest request);
}
