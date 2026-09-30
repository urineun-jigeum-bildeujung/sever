package com.golajugaenyang.product.application.inventory;


import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.product.application.inventory.port.in.dto.ReserveItemCommand;
import com.golajugaenyang.product.application.inventory.port.out.InventoryCommandRepository;
import com.golajugaenyang.product.application.inventory.port.out.StockMovementRepository;
import com.golajugaenyang.product.application.inventory.port.out.TimeDealStockCommandRepository;
import com.golajugaenyang.product.domain.inventory.StockMovement;
import com.golajugaenyang.product.domain.inventory.StockMovementType;
import com.golajugaenyang.product.domain.inventory.StockSubjectType;
import com.golajugaenyang.product.error.ProductErrorCode;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
public class InventoryCommandServiceTest {

    @Mock
    private InventoryCommandRepository inventoryCommandRepository;

    @Mock
    private TimeDealStockCommandRepository timeDealStockCommandRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    InventoryCommandService inventoryCommandService;

    @Test
    @DisplayName("동일 orderItemId+유형에 대해 같은 내용의 재요청은 멱등하게 성공 처리된다.")
    void treats_exact_duplicate_reserve_as_idempotent_success() {
        when(stockMovementRepository.recordIfAbsent(any())).thenReturn(false);
        when(stockMovementRepository.find(1L, StockMovementType.RESERVE))
            .thenReturn(Optional.of(
                StockMovement.of(
                    StockSubjectType.PRODUCT, 100L, 1L, StockMovementType.RESERVE, 2)));

        ReserveItemCommand command = new ReserveItemCommand(
            1L, StockSubjectType.PRODUCT, 100L, 2);

        assertThatCode(() -> inventoryCommandService.reserveBulk(List.of(command)))
            .doesNotThrowAnyException();

        verify(inventoryCommandRepository, never())
            .reserve(anyLong(), anyInt());
    }

    @Test
    @DisplayName("동일 orderItemId+유형에서 수량이 다른 요청은 STOCK_MOVEMENT_CONFLICT 예외가 발생한다.")
    void throws_conflict_when_duplicate_key_has_mismatched_quantity() {
        when(stockMovementRepository.recordIfAbsent(any())).thenReturn(false);
        when(stockMovementRepository.find(1L, StockMovementType.RESERVE))
            .thenReturn(Optional.of(
                StockMovement.of(
                    StockSubjectType.PRODUCT, 100L, 1L, StockMovementType.RESERVE, 2)));

        ReserveItemCommand command = new ReserveItemCommand(
            1L, StockSubjectType.PRODUCT, 100L, 5); // 수량 다름

        assertThatThrownBy(() -> inventoryCommandService.reserveBulk(List.of(command)))
            .isInstanceOf(AppException.class)
            .extracting(e -> ((AppException) e).getErrorCode())
            .isEqualTo(ProductErrorCode.STOCK_MOVEMENT_CONFLICT);
    }

    @Test
    @DisplayName("동일 orderItemId+유형에서 subjectId가 다른 요청은 STOCK_MOVEMENT_CONFLICT 예외가 발생한다.")
    void throws_conflict_when_duplicate_key_has_mismatched_subject() {
        when(stockMovementRepository.recordIfAbsent(any())).thenReturn(false);
        when(stockMovementRepository.find(1L, StockMovementType.RESERVE))
            .thenReturn(Optional.of(
                StockMovement.of(
                    StockSubjectType.PRODUCT, 100L, 1L, StockMovementType.RESERVE, 2)));

        ReserveItemCommand command = new ReserveItemCommand(
            1L, StockSubjectType.PRODUCT, 999L, 2);

        assertThatThrownBy(() -> inventoryCommandService.reserveBulk(List.of(command)))
            .isInstanceOf(AppException.class)
            .extracting(e -> ((AppException) e).getErrorCode())
            .isEqualTo(ProductErrorCode.STOCK_MOVEMENT_CONFLICT);
    }

    @Test
    @DisplayName("CONFIRM이 이미 기록된 주문 항목에 RELEASE를 시도하면 STOCK_MOVEMENT_CONFLICT 예외가 발생한다.")
    void throws_conflict_when_release_is_attempted_after_confirm() {
        when(stockMovementRepository.find(1L, StockMovementType.RESERVE))
            .thenReturn(Optional.of(StockMovement.of(
                StockSubjectType.PRODUCT, 100L, 1L, StockMovementType.RESERVE, 2)));
        when(stockMovementRepository.find(1L, StockMovementType.CONFIRM))
            .thenReturn(Optional.of(StockMovement.of(
                StockSubjectType.PRODUCT, 100L, 1L, StockMovementType.CONFIRM, 2)));

        assertThatThrownBy(() -> inventoryCommandService.release(
            StockSubjectType.PRODUCT, 100L, 1L, 2))
            .isInstanceOf(AppException.class)
            .extracting(e -> ((AppException) e).getErrorCode())
            .isEqualTo(ProductErrorCode.STOCK_MOVEMENT_CONFLICT);

        verify(inventoryCommandRepository, never()).release(anyLong(), anyInt());
    }

    @Test
    @DisplayName("RELEASE가 이미 기록된 주문 항목에 CONFIRM을 시도하면 STOCK_MOVEMENT_CONFLICT 예외가 발생한다.")
    void throws_conflict_when_confirm_is_attempted_after_release() {
        when(stockMovementRepository.find(1L, StockMovementType.RESERVE))
            .thenReturn(Optional.of(StockMovement.of(
                StockSubjectType.PRODUCT, 100L, 1L, StockMovementType.RESERVE, 2)));
        when(stockMovementRepository.find(1L, StockMovementType.RELEASE))
            .thenReturn(Optional.of(StockMovement.of(
                StockSubjectType.PRODUCT, 100L, 1L, StockMovementType.RELEASE, 2)));

        assertThatThrownBy(() -> inventoryCommandService.confirm(
            StockSubjectType.PRODUCT, 100L, 1L, 2))
            .isInstanceOf(AppException.class)
            .extracting(e -> ((AppException) e).getErrorCode())
            .isEqualTo(ProductErrorCode.STOCK_MOVEMENT_CONFLICT);
    }

    @Test
    @DisplayName("재고 이동은 행 갱신 전에 orderItemId 잠금을 먼저 획득한다.")
    void locks_order_item_before_updating_inventory_row() {
        when(stockMovementRepository.recordIfAbsent(any())).thenReturn(true);
        when(inventoryCommandRepository.reserve(100L, 2)).thenReturn(1);

        inventoryCommandService.reserveBulk(List.of(
            new ReserveItemCommand(1L, StockSubjectType.PRODUCT, 100L, 2)));

        InOrder inOrder = inOrder(stockMovementRepository, inventoryCommandRepository);
        inOrder.verify(stockMovementRepository, atLeastOnce()).lockOrderItem(1L);
        inOrder.verify(inventoryCommandRepository).reserve(100L, 2);
    }
    @Test
    @DisplayName("reserveBulk는 처리 전 모든 orderItemId를 orderItemId 오름차순으로 먼저 잠근다.")
    void locks_all_order_items_in_ascending_order_before_processing() {
        when(stockMovementRepository.recordIfAbsent(any())).thenReturn(true);
        when(inventoryCommandRepository.reserve(anyLong(), anyInt())).thenReturn(1);

        List<ReserveItemCommand> items = List.of(
            new ReserveItemCommand(5L, StockSubjectType.PRODUCT, 200L, 1),
            new ReserveItemCommand(2L, StockSubjectType.PRODUCT, 100L, 1)
        );

        inventoryCommandService.reserveBulk(items);

        InOrder inOrder = inOrder(stockMovementRepository);
        inOrder.verify(stockMovementRepository).lockOrderItem(2L);
        inOrder.verify(stockMovementRepository).lockOrderItem(5L);
    }
}
