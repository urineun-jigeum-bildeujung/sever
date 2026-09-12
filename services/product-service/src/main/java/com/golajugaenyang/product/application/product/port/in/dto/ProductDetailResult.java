package com.golajugaenyang.product.application.product.port.in.dto;

import com.golajugaenyang.common.core.domain.AllergenCode;
import com.golajugaenyang.common.core.domain.CautionIngredientCode;
import com.golajugaenyang.common.core.domain.QuantityUnit;
import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.domain.TargetAgeGroup;
import com.golajugaenyang.common.core.domain.TargetBreedSize;
import com.golajugaenyang.common.core.pricing.PriceCalculator;
import com.golajugaenyang.product.application.product.port.out.dto.ProductDetailProjection;
import com.golajugaenyang.product.domain.product.ProductStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ProductDetailResult(
    Long productId,
    List<String> images,
    String productName,
    BigDecimal price,
    BigDecimal originalPrice,
    BigDecimal discountRate,
    BigDecimal avgRating,
    Integer reviewCount,
    boolean soldOut,
    String manufacturer,
    String brandName,
    String originCountry,
    BigDecimal netQuantityValue,
    QuantityUnit netQuantityUnit,
    Set<String> ingredients,
    String feedingTarget,
    TargetBreedSize targetBreedSize,
    TargetAgeGroup targetAgeGroup,
    Set<Species> targetSpecies,
    String feedingMethod,
    Set<AllergenCode> allergenFlags,
    Set<CautionIngredientCode> cautionFlags,
    String consumptionPeriodDisplay,
    Integer shelfLifeAfterOpeningDays,
    String storageMethod
) {

    public static ProductDetailResult from(ProductDetailProjection p) {
        List<String> carousel = buildCarousel(p.thumbnailUrl(), p.imageUrls());

        return new ProductDetailResult(
            p.id(),
            carousel,
            p.productName(),
            p.price(),
            p.originalPrice(),
            PriceCalculator.discountRate(p.originalPrice(), p.price()),
            p.avgRating(),
            p.reviewCount(),
            p.status() == ProductStatus.SOLD_OUT,
            p.manufacturer(),
            p.brandName(),
            p.originCountry(),
            p.netQuantityValue(),
            p.netQuantityUnit(),
            p.ingredients(),
            p.feedingTarget(),
            p.targetBreedSize(),
            p.targetAgeGroup(),
            p.targetSpecies(),
            p.feedingMethod(),
            p.allergenFlags(),
            p.cautionFlags(),
            p.consumptionPeriodDisplay(),
            p.shelfLifeAfterOpeningDays(),
            p.storageMethod()
        );
    }

    private static List<String> buildCarousel(
        String thumbnailUrl, List<String> imageUrls
    ) {
        return Stream.concat(
            Stream.of(thumbnailUrl),
            imageUrls.stream().filter(url -> !url.equals(thumbnailUrl))
        ).collect(Collectors.toList());
    }
}
