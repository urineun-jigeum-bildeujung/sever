package com.golajugaenyang.product.adapter.out.persistence.inventory;


import com.golajugaenyang.product.application.inventory.port.out.StockMovementRepository;
import com.golajugaenyang.product.domain.inventory.StockMovement;
import com.golajugaenyang.product.domain.inventory.StockMovementType;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class StockMovementRepositoryImpl implements StockMovementRepository {

    private final StockMovementJpaRepository jpaRepository;

    @Override
    public boolean recordIfAbsent(StockMovement movement) {
        try {
            jpaRepository.saveAndFlush(movement);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    @Override
    public Optional<StockMovement> findPreceding(Long orderItemId, StockMovementType movementType) {
        return jpaRepository.findByOrderItemIdAndMovementType(orderItemId, movementType);
    }
}
