package com.golajugaenyang.product.application.inventory;


import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
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
}
