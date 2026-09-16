package com.golajugaenyang.order.adapter.in.web.cart;

import com.golajugaenyang.order.adapter.in.web.cart.dto.AddCartItemRequest;
import com.golajugaenyang.order.adapter.in.web.cart.dto.CartResponse;
import com.golajugaenyang.order.adapter.in.web.cart.dto.ChangeCartItemQuantityRequest;
import com.golajugaenyang.order.domain.cart.CartItemType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;


@Tag(name = "Cart", description = "장바구니 API")
public interface CartControllerDocs {

    @Operation(
        summary = "장바구니 조회",
        description = "회원의 장바구니 상품 목록과, 구매 가능한 상품만 합산한 총 금액을 반환한다. "
            + "품절/판매중지/타임딜 종료 상품은 available=false로 표시되며 합계에서 제외된다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<CartResponse> getCart(
        @Parameter(description = "회원 ID", required = true, example = "1")
        Long memberId
    );

    @Operation(
        summary = "장바구니에 상품 추가",
        description = "이미 담긴 상품이면 기존 수량에 더해지며, 합산 결과는 최대 99개로 클램프된다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "추가 성공"),
        @ApiResponse(responseCode = "409", description = "구매 불가능한 상품")
    })
    void addItem(
        @Parameter(description = "회원 ID", required = true, example = "1") Long memberId,
        @Valid AddCartItemRequest request
    );

    @Operation(
        summary = "장바구니 상품 수량 변경",
        description = "delta만큼 수량을 증감시킨다(예: +1, -1)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "변경 성공"),
        @ApiResponse(responseCode = "404", description = "장바구니에 해당 상품이 존재하지 않음")
    })
    void changeQuantity(
        @Parameter(description = "회원 ID", required = true, example = "1") Long memberId,
        @Parameter(description = "상품 유형", required = true, example = "NORMAL") CartItemType itemType,
        @Parameter(description = "상품 ID 또는 타임딜 아이템 ID", required = true, example = "1001") Long itemId,
        @Valid ChangeCartItemQuantityRequest request
    );

    @Operation(
        summary = "장바구니 상품 삭제",
        description = "존재하지 않는 상품을 삭제 요청해도 멱등하게 204를 반환한다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공")
    })
    void removeItem(
        @Parameter(description = "회원 ID", required = true, example = "1") Long memberId,
        @Parameter(description = "상품 유형", required = true, example = "NORMAL") CartItemType itemType,
        @Parameter(description = "상품 ID 또는 타임딜 아이템 ID", required = true, example = "1001") Long itemId
    );
}
