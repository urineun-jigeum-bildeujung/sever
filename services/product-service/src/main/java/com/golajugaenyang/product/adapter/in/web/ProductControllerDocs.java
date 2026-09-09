package com.golajugaenyang.product.adapter.in.web;

import com.golajugaenyang.common.core.domain.CategoryCode;
import com.golajugaenyang.product.adapter.in.web.dto.ProductListResponse;
import com.golajugaenyang.product.domain.product.ProductSortType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;


@Tag(name = "Product", description = "상품 조회 API")
public interface ProductControllerDocs {

    @Operation(
        summary = "상품 목록 조회",
        description = "카테고리별 상품 목록을 커서 기반 무한 스크롤로 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음")
    })
    ResponseEntity<ProductListResponse> getProducts(
        @Parameter(description = "카테고리 코드. 생략 시 전체 카테고리 대상")
        CategoryCode category,

        @Parameter(description = "정렬 기준. 기본값은 POPULAR (RECOMMEND는 현재 POPULAR로 대체 처리)")
        ProductSortType sort,

        @Parameter(description = "이전 응답의 nextCursor 값. 첫 페이지 조회 시 생략")
        String cursor,

        @Parameter(description = "페이지당 조회 개수. 생략 시 서버 기본값 적용")
        Integer size,

        @Parameter(description = "추천 정렬용 반려동물 ID (현재 미사용, AI 추천 API 연동 후 사용 예정)")
        Long petId
    );
}
