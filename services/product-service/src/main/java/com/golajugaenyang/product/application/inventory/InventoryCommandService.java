package com.golajugaenyang.product.application.inventory;


import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.product.application.inventory.port.in.InventoryCommandUseCase;
import com.golajugaenyang.product.application.inventory.port.in.dto.ReserveItemCommand;
import com.golajugaenyang.product.application.inventory.port.out.InventoryCommandRepository;
import com.golajugaenyang.product.application.inventory.port.out.StockMovementRepository;
import com.golajugaenyang.product.application.inventory.port.out.TimeDealStockCommandRepository;
import com.golajugaenyang.product.domain.inventory.StockMovement;
import com.golajugaenyang.product.domain.inventory.StockMovementType;
import com.golajugaenyang.product.domain.inventory.StockSubjectType;
import com.golajugaenyang.product.error.ProductErrorCode;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryCommandService implements InventoryCommandUseCase {

    private final InventoryCommandRepository inventoryCommandRepository;
    private final TimeDealStockCommandRepository timeDealStockCommandRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void reserveBulk(List<ReserveItemCommand> items) {
        List<ReserveItemCommand> sorted = items.stream()
            .sorted(Comparator.comparing(ReserveItemCommand::subjectType)
                .thenComparing(ReserveItemCommand::subjectId))
            .toList();

        for (ReserveItemCommand item : sorted) {
            boolean success = applyMovement(
                item.subjectType(), item.subjectId(), item.orderItemId(),
                StockMovementType.RESERVE, item.quantity());

            if (!success) {
                log.info(
                    "[InventoryReservation] 재고 부족. subjectType={}, subjectId={}, requestedQuantity={}",
                    item.subjectType(), item.subjectId(), item.quantity());
                throw new AppException(ProductErrorCode.INSUFFICIENT_STOCK);
            }
        }
    }

    @Override
    @Transactional
    public void confirm(StockSubjectType type, Long subjectId, Long orderItemId, int quantity) {
        applyMovementOrThrow(type, subjectId, orderItemId, StockMovementType.CONFIRM, quantity);
    }

    @Override
    @Transactional
    public void release(StockSubjectType type, Long subjectId, Long orderItemId, int quantity) {
        applyMovementOrThrow(type, subjectId, orderItemId, StockMovementType.RELEASE, quantity);
    }

    @Override
    @Transactional
    public void restore(StockSubjectType type, Long subjectId, Long orderItemId, int quantity) {
        applyMovementOrThrow(type, subjectId, orderItemId, StockMovementType.RESTORE, quantity);
    }

    private void applyMovementOrThrow(
        StockSubjectType type, Long subjectId, Long orderItemId,
        StockMovementType movementType, int quantity
    ) {
        boolean success = applyMovement(type, subjectId, orderItemId, movementType, quantity);
        if (!success) {
            throw new AppException(ProductErrorCode.STOCK_MOVEMENT_CONFLICT);
        }
    }

    private boolean applyMovement(
        StockSubjectType subjectType, Long subjectId, Long orderItemId,
        StockMovementType type, int quantity
    ) {
        // 이력 검증
        if (type.requiresPrecedingMovement()) {
            int precedingQuantity = stockMovementRepository
                .findQuantity(orderItemId, type.getRequiredPrecedingType())
                .orElse(0);

            if (precedingQuantity != quantity) {
                log.warn(
                    "[InventoryCommand] 선행 이력 오류. orderItemId={}, type={}", orderItemId, type);
                throw new AppException(ProductErrorCode.STOCK_MOVEMENT_PRECONDITION_NOT_MET);
            }
        }

        // 이력 삽입
        boolean recorded = stockMovementRepository.recordIfAbsent(
            StockMovement.of(subjectType, subjectId, orderItemId, type, quantity));
        if (!recorded) {
            return true;
        }

        // 원자적 업데이트
        int affected = dispatchUpdate(subjectType, subjectId, type, quantity);
        if (affected == 0) {
            return false;
        }

        // 상태 동기화 이벤트 발행
        eventPublisher.publishEvent(new StockChangedEvent(subjectType, subjectId));
        return true;
    }

    private int dispatchUpdate(
        StockSubjectType subjectType, Long subjectId,
        StockMovementType type, int quantity
    ) {
        return switch (subjectType) {
            case PRODUCT -> switch (type) {
                case RESERVE -> inventoryCommandRepository.reserve(subjectId, quantity);
                case CONFIRM -> inventoryCommandRepository.confirm(subjectId, quantity);
                case RELEASE -> inventoryCommandRepository.release(subjectId, quantity);
                case RESTORE -> inventoryCommandRepository.restore(subjectId, quantity);
            };
            case TIME_DEAL_ITEM -> switch (type) {
                case RESERVE -> timeDealStockCommandRepository.reserve(subjectId, quantity);
                case CONFIRM -> timeDealStockCommandRepository.confirm(subjectId, quantity);
                case RELEASE -> timeDealStockCommandRepository.release(subjectId, quantity);
                case RESTORE -> timeDealStockCommandRepository.restore(subjectId, quantity);
            };
        };
    }
}
