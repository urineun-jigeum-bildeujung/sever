package com.golajugaenyang.product.adapter.out.persistence.inventory;


import static com.golajugaenyang.product.domain.inventory.QInventory.inventory;

import com.golajugaenyang.product.application.inventory.port.out.InventoryCommandRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class InventoryCommandRepositoryImpl implements InventoryCommandRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public int reserve(Long productId, int quantity) {
        return (int) queryFactory.update(inventory)
            .set(inventory.reservedStock, inventory.reservedStock.add(quantity))
            .where(inventory.productId.eq(productId)
                .and(inventory.totalStock.subtract(inventory.reservedStock).goe(quantity)))
            .execute();
    }

    @Override
    public int confirm(Long productId, int quantity) {
        return (int) queryFactory.update(inventory)
            .set(inventory.totalStock, inventory.totalStock.subtract(quantity))
            .set(inventory.reservedStock, inventory.reservedStock.subtract(quantity))
            .where(inventory.productId.eq(productId).and(inventory.reservedStock.goe(quantity)))
            .execute();
    }

    @Override
    public int release(Long productId, int quantity) {
        return (int) queryFactory.update(inventory)
            .set(inventory.reservedStock, inventory.reservedStock.subtract(quantity))
            .where(inventory.productId.eq(productId).and(inventory.reservedStock.goe(quantity)))
            .execute();
    }

    @Override
    public int restore(Long productId, int quantity) {
        return (int) queryFactory.update(inventory)
            .set(inventory.totalStock, inventory.totalStock.add(quantity))
            .where(inventory.productId.eq(productId))
            .execute();
    }
}
