package com.golajugaenyang.product.adapter.in.web.timedeal;

import com.golajugaenyang.product.adapter.in.web.timedeal.dto.TimeDealListResponse;
import com.golajugaenyang.product.domain.timedeal.TimeDealStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;


@Tag(name = "TimeDeal", description = "타임딜 조회 API")
public interface TimeDealListControllerDocs {

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
}
