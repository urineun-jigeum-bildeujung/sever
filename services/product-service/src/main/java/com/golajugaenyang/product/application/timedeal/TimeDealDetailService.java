package com.golajugaenyang.product.application.timedeal;


import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.product.application.product.port.in.ProductDetailUseCase;
import com.golajugaenyang.product.application.product.port.in.dto.ProductDetailResult;
import com.golajugaenyang.product.application.timedeal.port.in.TimeDealDetailUseCase;
import com.golajugaenyang.product.application.timedeal.port.out.TimeDealPricingRepository;
import com.golajugaenyang.product.application.timedeal.port.out.dto.TimeDealPricingProjection;
import com.golajugaenyang.product.domain.timedeal.TimeDealItemStatus;
import com.golajugaenyang.product.error.ProductErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TimeDealDetailService implements TimeDealDetailUseCase {

    private final TimeDealPricingRepository timeDealPricingRepository;
    private final ProductDetailUseCase productDetailUseCase;

    @Override
    @Transactional(readOnly = true)
    public ProductDetailResult getTimeDealDetail(Long timeDealItemId) {
        TimeDealPricingProjection pricing = timeDealPricingRepository
            .findByTimeDealItemId(timeDealItemId)
            .orElseThrow(() -> new AppException(ProductErrorCode.TIME_DEAL_ITEM_NOT_FOUND));

        if (!TimeDealVisibility.isListable(pricing.dealStatus())) {
            throw new AppException(ProductErrorCode.TIME_DEAL_ITEM_NOT_FOUND);
        }

        ProductDetailResult baseDetail =
            productDetailUseCase.getProductDetail(pricing.productId());

        int remaining = Math.max(
            pricing.quantityLimit() - pricing.reservedQuantity() - pricing.soldQuantity(), 0);
        boolean soldOut = pricing.itemStatus() == TimeDealItemStatus.SOLD_OUT || remaining == 0;

        return baseDetail.withPricing(
            pricing.discountedPrice(), pricing.normalPrice(),
            pricing.discountRate(), soldOut);
    }
}
