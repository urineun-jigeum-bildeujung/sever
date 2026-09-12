package com.golajugaenyang.product.application.product;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.product.application.product.port.in.ProductDetailUseCase;
import com.golajugaenyang.product.application.product.port.in.dto.ProductDetailResult;
import com.golajugaenyang.product.application.product.port.out.ProductDetailQueryRepository;
import com.golajugaenyang.product.application.product.port.out.dto.ProductDetailProjection;
import com.golajugaenyang.product.error.ProductErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductDetailService implements ProductDetailUseCase {

    private final ProductDetailQueryRepository productDetailQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public ProductDetailResult getProductDetail(Long productId) {
        ProductDetailProjection projection = productDetailQueryRepository
            .findDetailById(productId)
            .orElseThrow(() -> new AppException(ProductErrorCode.PRODUCT_NOT_FOUND));
        return ProductDetailResult.from(projection);
    }
}
