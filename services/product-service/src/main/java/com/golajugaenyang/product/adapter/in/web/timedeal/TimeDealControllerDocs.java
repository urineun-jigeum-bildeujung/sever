package com.golajugaenyang.product.adapter.in.web.timedeal;

import com.golajugaenyang.product.adapter.in.web.product.dto.ProductDetailResponse;
import com.golajugaenyang.product.adapter.in.web.timedeal.dto.TimeDealListResponse;
import com.golajugaenyang.product.domain.timedeal.TimeDealStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;


@Tag(name = "TimeDeal", description = "타임딜 조회 API")
public interface TimeDealControllerDocs {

    @Operation(
        summary = "타임딜 목록 조회",
        description = "진행중(ACTIVE) 또는 오픈예정(SCHEDULED) 딜과 그에 속한 상품 목록을 조회합니다. "
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "status가 ACTIVE/SCHEDULED가 아님")
    })
    ResponseEntity<TimeDealListResponse> getTimeDeals(
        @Parameter(description = "ACTIVE(진행중) 또는 SCHEDULED(오픈예정)") TimeDealStatus status
    );

    @Operation(
        summary = "타임딜 상품 상세 조회",
        description = "일반 상품 상세 조회와 응답 구조가 동일합니다. "
            + "가격 관련 필드만 해당 타임딜의 할인가/정가/할인율로 대체됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않거나 노출 중이 아닌 타임딜 상품")
    })
    ResponseEntity<ProductDetailResponse> getTimeDealDetail(
        @Parameter(description = "타임딜 아이템 ID") @Min(1) Long timeDealItemId
    );
}
