package com.golajugaenyang.order.application.cart;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.order.application.cart.port.in.dto.AddCartItemCommand;
import com.golajugaenyang.order.application.cart.port.in.dto.CartItemResult;
import com.golajugaenyang.order.application.cart.port.in.dto.CartResult;
import com.golajugaenyang.order.application.cart.port.in.dto.ChangeCartItemQuantityCommand;
import com.golajugaenyang.order.application.cart.port.in.dto.RemoveCartItemCommand;
import com.golajugaenyang.order.application.cart.port.out.CartRepository;
import com.golajugaenyang.order.application.cart.port.out.ProductCatalogPort;
import com.golajugaenyang.order.application.cart.port.out.dto.CartCatalogLookupResult;
import com.golajugaenyang.order.application.cart.port.out.dto.ProductSummary;
import com.golajugaenyang.order.application.cart.port.out.dto.TimeDealSummary;
import com.golajugaenyang.order.domain.cart.CartItemKey;
import com.golajugaenyang.order.domain.cart.CartItemType;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductCatalogPort productCatalogPort;

    private CartService cartService;

    private static final Long MEMBER_ID = 1L;
    private static final Long PRODUCT_ID = 100L;
    private static final Long TIME_DEAL_ITEM_ID = 200L;

    @BeforeEach
    void setUp() {
        cartService = new CartService(cartRepository, productCatalogPort);
    }

    @Nested
    class GetCartTest {

        @Test
        @DisplayName("장바구니에 담긴 상품이 없으면 빈 결과를 반환한다.")
        void getCart_returns_empty_result_when_cart_has_no_items() {
            given(cartRepository.findAll(MEMBER_ID)).willReturn(Map.of());

            CartResult result = cartService.getCart(MEMBER_ID);

            assertThat(result.items()).isEmpty();
            assertThat(result.totalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("구매 가능한 일반 상품은 available=true와 소계 금액을 포함해 반환한다.")
        void getCart_returns_available_item_with_subtotal_when_product_is_purchasable() {
            CartItemKey key = new CartItemKey(CartItemType.NORMAL, PRODUCT_ID);
            given(cartRepository.findAll(MEMBER_ID)).willReturn(Map.of(key, 3));

            ProductSummary summary = new ProductSummary(
                PRODUCT_ID, "테스트 상품", "http://thumbnail",
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), BigDecimal.ZERO,
                true, "ON_SALE"
            );
            given(productCatalogPort.lookup(List.of(PRODUCT_ID), List.of()))
                .willReturn(new CartCatalogLookupResult(
                    Map.of(PRODUCT_ID, summary), Map.of(), List.of(), List.of(), List.of(),
                    List.of()));

            CartResult result = cartService.getCart(MEMBER_ID);

            assertThat(result.items()).hasSize(1);
            CartItemResult item = result.items().getFirst();
            assertThat(item.available()).isTrue();
            assertThat(item.unavailableReason()).isNull();
            assertThat(item.subtotal()).isEqualByComparingTo(BigDecimal.valueOf(3000));
            assertThat(result.totalAmount()).isEqualByComparingTo(BigDecimal.valueOf(3000));
        }

        @Test
        @DisplayName("카탈로그 조회 결과에 없는 상품은 NOT_FOUND 사유로 표시되고 합계에서 제외된다.")
        void getCart_marks_item_as_not_found_when_missing_from_catalog_result() {
            CartItemKey key = new CartItemKey(CartItemType.NORMAL, PRODUCT_ID);
            given(cartRepository.findAll(MEMBER_ID)).willReturn(Map.of(key, 1));
            given(productCatalogPort.lookup(List.of(PRODUCT_ID), List.of()))
                .willReturn(new CartCatalogLookupResult(
                    Map.of(), Map.of(), List.of(PRODUCT_ID), List.of(), List.of(), List.of()));

            CartResult result = cartService.getCart(MEMBER_ID);

            CartItemResult item = result.items().getFirst();
            assertThat(item.available()).isFalse();
            assertThat(item.unavailableReason()).isEqualTo(CartItemResult.REASON_NOT_FOUND);
            assertThat(result.totalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("호출 자체가 실패한 상품은 삭제(NOT_FOUND)가 아니라 일시적 조회불가로 표시된다.")
        void getCart_marks_item_as_temporarily_unavailable_when_catalog_call_is_unreachable() {
            CartItemKey key = new CartItemKey(CartItemType.NORMAL, PRODUCT_ID);
            given(cartRepository.findAll(MEMBER_ID)).willReturn(Map.of(key, 1));
            given(productCatalogPort.lookup(List.of(PRODUCT_ID), List.of()))
                .willReturn(new CartCatalogLookupResult(
                    Map.of(), Map.of(), List.of(), List.of(), List.of(PRODUCT_ID), List.of()));

            CartResult result = cartService.getCart(MEMBER_ID);

            CartItemResult item = result.items().getFirst();
            assertThat(item.available()).isFalse();
            assertThat(item.unavailableReason()).isEqualTo(
                CartItemResult.REASON_TEMPORARILY_UNAVAILABLE);
        }

        @Test
        @DisplayName("종료 시각이 지난 타임딜 상품은 DEAL_ENDED 사유로 표시된다.")
        void getCart_marks_time_deal_item_as_deal_ended_when_deal_end_time_has_passed() {
            CartItemKey key = new CartItemKey(CartItemType.TIME_DEAL, TIME_DEAL_ITEM_ID);
            given(cartRepository.findAll(MEMBER_ID)).willReturn(Map.of(key, 1));

            TimeDealSummary summary = new TimeDealSummary(
                TIME_DEAL_ITEM_ID, PRODUCT_ID, "타임딜 상품", "http://thumbnail",
                BigDecimal.valueOf(800), BigDecimal.valueOf(1000), BigDecimal.valueOf(20),
                10, 5, true, "ON_SALE",
                OffsetDateTime.now().minusMinutes(1) // 이미 종료된 타임딜
            );
            given(productCatalogPort.lookup(List.of(), List.of(TIME_DEAL_ITEM_ID)))
                .willReturn(new CartCatalogLookupResult(
                    Map.of(), Map.of(TIME_DEAL_ITEM_ID, summary), List.of(), List.of(), List.of(),
                    List.of()));

            CartResult result = cartService.getCart(MEMBER_ID);

            CartItemResult item = result.items().getFirst();
            assertThat(item.available()).isFalse();
            assertThat(item.unavailableReason()).isEqualTo(CartItemResult.REASON_DEAL_ENDED);
        }

        @Test
        @DisplayName("품절 등으로 구매 불가능하지만 상품 정보는 존재하는 상품도 이름/썸네일/가격을 포함해 반환한다.")
        void getCart_includes_product_info_when_item_exists_but_not_purchasable() {
            CartItemKey key = new CartItemKey(CartItemType.NORMAL, PRODUCT_ID);
            given(cartRepository.findAll(MEMBER_ID)).willReturn(Map.of(key, 2));

            ProductSummary summary = new ProductSummary(
                PRODUCT_ID, "품절된 상품", "http://thumbnail",
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), BigDecimal.ZERO,
                false, "SOLD_OUT"
            );
            given(productCatalogPort.lookup(List.of(PRODUCT_ID), List.of()))
                .willReturn(new CartCatalogLookupResult(
                    Map.of(PRODUCT_ID, summary), Map.of(), List.of(), List.of(), List.of(), List.of()));

            CartResult result = cartService.getCart(MEMBER_ID);

            CartItemResult item = result.items().getFirst();
            assertThat(item.available()).isFalse();
            assertThat(item.unavailableReason()).isEqualTo("SOLD_OUT");
            assertThat(item.productName()).isEqualTo("품절된 상품");
            assertThat(item.thumbnailUrl()).isEqualTo("http://thumbnail");
            assertThat(item.price()).isEqualByComparingTo(BigDecimal.valueOf(1000));
            assertThat(item.subtotal()).isNull();
            assertThat(result.totalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    class AddItemTest {

        @Test
        @DisplayName("구매 가능한 상품을 추가하면 CartRepository에 추가/증가 요청이 위임된다.")
        void addItem_delegates_to_repository_when_product_is_purchasable() {
            ProductSummary summary = new ProductSummary(
                PRODUCT_ID, "테스트 상품", "http://thumbnail",
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), BigDecimal.ZERO,
                true, "ON_SALE"
            );
            given(productCatalogPort.lookup(List.of(PRODUCT_ID), List.of()))
                .willReturn(new CartCatalogLookupResult(
                    Map.of(PRODUCT_ID, summary), Map.of(), List.of(), List.of(), List.of(),
                    List.of()));

            cartService.addItem(
                new AddCartItemCommand(MEMBER_ID, CartItemType.NORMAL, PRODUCT_ID, 2));

            verify(cartRepository, times(1)).addOrIncrease(
                eq(MEMBER_ID),
                eq(new CartItemKey(CartItemType.NORMAL, PRODUCT_ID)),
                eq(2),
                any(Duration.class)
            );
        }

        @Test
        @DisplayName("구매 불가능한 상품을 추가하려고 하면 예외가 던져지고 저장소는 호출되지 않는다.")
        void addItem_throws_product_not_purchasable_exception_when_product_is_not_purchasable() {
            ProductSummary summary = new ProductSummary(
                PRODUCT_ID, "품절 상품", "http://thumbnail",
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), BigDecimal.ZERO,
                false, "SOLD_OUT"
            );
            given(productCatalogPort.lookup(List.of(PRODUCT_ID), List.of()))
                .willReturn(new CartCatalogLookupResult(
                    Map.of(PRODUCT_ID, summary), Map.of(), List.of(), List.of(), List.of(),
                    List.of()));

            assertThatThrownBy(() -> cartService.addItem(
                new AddCartItemCommand(MEMBER_ID, CartItemType.NORMAL, PRODUCT_ID, 1)))
                .isInstanceOf(AppException.class);

            verify(cartRepository, never()).addOrIncrease(any(), any(), anyInt(), any());
        }

        @Test
        @DisplayName("상품 조회 자체가 실패하면 예외가 던져지고 발생하고 저장소는 호출되지 않는다.")
        void addItem_throws_product_catalog_unavailable_exception_when_catalog_call_fails() {
            given(productCatalogPort.lookup(List.of(PRODUCT_ID), List.of()))
                .willReturn(new CartCatalogLookupResult(
                    Map.of(), Map.of(), List.of(), List.of(), List.of(PRODUCT_ID), List.of()));

            assertThatThrownBy(() -> cartService.addItem(
                new AddCartItemCommand(MEMBER_ID, CartItemType.NORMAL, PRODUCT_ID, 1)))
                .isInstanceOf(AppException.class);

            verify(cartRepository, never()).addOrIncrease(any(), any(), anyInt(), any());
        }

    }

    @Nested
    class ChangeQuantityTest {

        @Test
        @DisplayName("수량 변경 요청이 저장소에 위임되어 정상 처리된다.")
        void changeQuantity_delegates_to_repository_with_given_delta() {
            CartItemKey key = new CartItemKey(CartItemType.NORMAL, PRODUCT_ID);
            given(cartRepository.changeQuantity(eq(MEMBER_ID), eq(key), eq(1), any(Duration.class)))
                .willReturn(5);

            cartService.changeQuantity(
                new ChangeCartItemQuantityCommand(MEMBER_ID, CartItemType.NORMAL, PRODUCT_ID, 1));

            verify(cartRepository, times(1))
                .changeQuantity(eq(MEMBER_ID), eq(key), eq(1), any(Duration.class));
        }

        @Test
        @DisplayName("장바구니에 없는 상품의 수량을 변경하려 하면 예외를 던진다.")
        void changeQuantity_throws_cart_item_not_found_exception_when_repository_returns_not_found() {
            CartItemKey key = new CartItemKey(CartItemType.NORMAL, PRODUCT_ID);
            given(cartRepository.changeQuantity(eq(MEMBER_ID), eq(key), eq(1), any(Duration.class)))
                .willReturn(CartRepository.NOT_FOUND);

            assertThatThrownBy(() -> cartService.changeQuantity(
                new ChangeCartItemQuantityCommand(MEMBER_ID, CartItemType.NORMAL, PRODUCT_ID, 1)))
                .isInstanceOf(AppException.class);
        }
    }

    @Nested
    class RemoveItemTest {

        @Test
        @DisplayName("상품 삭제 요청이 저장소에 위임된다.")
        void removeItem_delegates_to_repository() {
            cartService.removeItem(
                new RemoveCartItemCommand(MEMBER_ID, CartItemType.NORMAL, PRODUCT_ID));

            verify(cartRepository, times(1))
                .remove(MEMBER_ID, new CartItemKey(CartItemType.NORMAL, PRODUCT_ID));
        }
    }

}
