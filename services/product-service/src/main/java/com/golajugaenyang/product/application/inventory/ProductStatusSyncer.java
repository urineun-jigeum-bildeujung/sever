package com.golajugaenyang.product.application.inventory;

import com.golajugaenyang.product.application.inventory.port.out.ProductStatusCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class ProductStatusSyncer {

    private final ProductStatusCommandRepository productStatusCommandRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sync(Long productId) {
        int soldOut = productStatusCommandRepository.markSoldOutIfStillOutOfStock(productId);
        if (soldOut == 0) {
            productStatusCommandRepository.markOnSaleIfStillInStock(productId);
        }
    }
}
