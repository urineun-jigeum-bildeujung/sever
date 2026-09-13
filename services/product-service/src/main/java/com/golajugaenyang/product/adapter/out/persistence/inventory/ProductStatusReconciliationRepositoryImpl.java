package com.golajugaenyang.product.adapter.out.persistence.inventory;

import static com.golajugaenyang.product.domain.inventory.QInventory.inventory;
import static com.golajugaenyang.product.domain.product.QProduct.product;

import com.golajugaenyang.product.application.inventory.port.out.ProductStatusReconciliationRepository;
import com.golajugaenyang.product.domain.product.ProductStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductStatusReconciliationRepositoryImpl implements
    ProductStatusReconciliationRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Long> findStatusMismatchedProductIds() {
        return queryFactory
            .select(product.id)
            .from(product)
            .join(inventory).on(inventory.productId.eq(product.id))
            .where(inventory.totalStock.subtract(inventory.reservedStock)
                .loe(0)
                .and(product.status.eq(ProductStatus.ON_SALE))
                .or(
                    inventory.totalStock.subtract(inventory.reservedStock)
                        .gt(0)
                        .and(product.status.eq(ProductStatus.SOLD_OUT))
                )
            )
            .fetch();
    }
}
