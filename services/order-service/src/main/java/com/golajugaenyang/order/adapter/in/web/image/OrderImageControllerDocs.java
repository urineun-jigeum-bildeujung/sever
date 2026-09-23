package com.golajugaenyang.order.adapter.in.web.image;


import com.golajugaenyang.common.security.annotation.MemberId;
import com.golajugaenyang.order.adapter.in.web.image.dto.OrderImageUploadRequest;
import com.golajugaenyang.order.adapter.in.web.image.dto.OrderImageUploadResponse;
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

@Tag(name = "Order", description = "주문 관련 API")
public interface OrderImageControllerDocs {

    @Operation(
        summary = "이미지 업로드용 Presigned URL 발급",
        description = "반품/교환 신청 등에 첨부할 이미지를 업로드하기 위한 presigned URL을 발급합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "발급 성공",
            content = @Content(schema = @Schema(implementation = OrderImageUploadResponse.class))),
        @ApiResponse(responseCode = "400", description = "지원하지 않는 확장자",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE))
    })
    ResponseEntity<OrderImageUploadResponse> issueImageUploadUrl(
        @MemberId Long memberId,
        @Valid @RequestBody OrderImageUploadRequest request);
}
