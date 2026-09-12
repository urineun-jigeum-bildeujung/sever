package com.golajugaenyang.product.application.product.port.out.dto;

import com.golajugaenyang.common.core.domain.AllergenCode;
import com.golajugaenyang.common.core.domain.CautionIngredientCode;
import com.golajugaenyang.common.core.domain.QuantityUnit;
import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.domain.TargetAgeGroup;
import com.golajugaenyang.common.core.domain.TargetBreedSize;
import com.golajugaenyang.product.domain.product.ProductStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record ProductDetailProjection(
    Long id,
    String thumbnailUrl,
    List<String> imageUrls,
    String productName,
    BigDecimal price,
    BigDecimal originalPrice,
    BigDecimal avgRating,
    Integer reviewCount,
    ProductStatus status,
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

}
