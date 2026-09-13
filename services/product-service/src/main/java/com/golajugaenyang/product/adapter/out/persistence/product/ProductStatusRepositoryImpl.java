package com.golajugaenyang.product.adapter.out.persistence.product;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.product.application.inventory.port.out.ProductStatusRepository;
import com.golajugaenyang.product.domain.product.Product;
import com.golajugaenyang.product.error.ProductErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductStatusRepositoryImpl implements ProductStatusRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product findById(Long productId) {
        return productJpaRepository.findById(productId)
            .orElseThrow(() -> new AppException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }
}
