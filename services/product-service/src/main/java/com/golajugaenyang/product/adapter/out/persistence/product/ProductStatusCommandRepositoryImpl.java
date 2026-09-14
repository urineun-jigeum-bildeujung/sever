package com.golajugaenyang.product.adapter.out.persistence.product;

import static com.golajugaenyang.product.domain.inventory.QInventory.inventory;
import static com.golajugaenyang.product.domain.product.QProduct.product;

import com.golajugaenyang.product.application.inventory.port.out.ProductStatusCommandRepository;
import com.golajugaenyang.product.domain.product.ProductStatus;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class ProductStatusCommandRepositoryImpl implements ProductStatusCommandRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public int markSoldOutIfStillOutOfStock(Long productId) {
        return (int) queryFactory.update(product)
            .set(product.status, ProductStatus.SOLD_OUT)
            .where(product.id.eq(productId)
                .and(product.status.eq(ProductStatus.ON_SALE))
                .and(JPAExpressions
                    .select(inventory.totalStock.subtract(inventory.reservedStock))
                    .from(inventory)
                    .where(inventory.productId.eq(productId))
                    .loe(0)))
            .execute();
    }

    @Override
    public int markOnSaleIfStillInStock(Long productId) {
        return (int) queryFactory.update(product)
            .set(product.status, ProductStatus.ON_SALE)
            .where(product.id.eq(productId)
                .and(product.status.eq(ProductStatus.SOLD_OUT))
                .and(JPAExpressions
                    .select(inventory.totalStock.subtract(inventory.reservedStock))
                    .from(inventory)
                    .where(inventory.productId.eq(productId))
                    .gt(0)))
            .execute();
    }

}
