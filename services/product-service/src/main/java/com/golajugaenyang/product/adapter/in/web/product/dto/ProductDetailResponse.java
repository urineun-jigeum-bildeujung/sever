package com.golajugaenyang.product.adapter.in.web.product.dto;

import com.golajugaenyang.common.core.domain.CautionIngredientCode;
import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.product.application.product.port.in.dto.ProductDetailResult;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record ProductDetailResponse(
    Long productId,
    TimeDealResponse timeDeal,
    Summary summary,
    DetailInfo detailInfo
) {

    public record TimeDealResponse(
        Long timeDealItemId,
        Long dealId,
        String dealStatus,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        OffsetDateTime serverTime,
        boolean purchasable
    ) {

    }

    public record Summary(
        List<String> images,
        String productName,
        BigDecimal price,
        BigDecimal originalPrice,
        BigDecimal discountRate,
        BigDecimal avgRating,
        Integer reviewCount,
        boolean soldOut
    ) {

    }

    public record DetailInfo(
        String manufacturer,
        String brandName,
        String originCountry,
        BigDecimal netQuantityValue,
        String netQuantityUnit,
        List<String> ingredients,
        String feedingTarget,
        String targetBreedSize,
        String targetAgeGroup,
        Set<String> targetSpecies,
        String feedingMethod,
        List<AllergenInfo> allergens,
        List<String> cautions,
        String consumptionPeriodDisplay,
        Integer shelfLifeAfterOpeningDays,
        String storageMethod
    ) {

    }

    public record AllergenInfo(
        String code,
        String displayName,
        String severity
    ) {

    }

    public static ProductDetailResponse from(ProductDetailResult r) {
        TimeDealResponse timeDeal = r.timeDeal() == null ? null
            : new TimeDealResponse(
                r.timeDeal().timeDealItemId(), r.timeDeal().dealId(),
                r.timeDeal().dealStatus().name(),
                r.timeDeal().startAt(), r.timeDeal().endAt(), r.timeDeal().serverTime(),
                r.timeDeal().purchasable());

        Summary summary = new Summary(
            r.images(), r.productName(), r.price(), r.originalPrice(),
            r.discountRate(),
            r.avgRating(), r.reviewCount(), r.soldOut());

        List<AllergenInfo> allergens = r.allergenFlags().stream()
            .map(a -> new AllergenInfo(
                a.name(),
                a.getDisplayName(),
                a.getSeverity().name())
            ).toList();

        List<String> cautions = r.cautionFlags().stream()
            .map(CautionIngredientCode::getDisplayName)
            .toList();

        Set<String> speciesNames = r.targetSpecies().stream()
            .map(Species::getDisplayName)
            .collect(Collectors.toSet());

        DetailInfo detailInfo = new DetailInfo(
            r.manufacturer(),
            r.brandName(),
            r.originCountry(),
            r.netQuantityValue(),
            r.netQuantityUnit() != null ? r.netQuantityUnit().getSymbol() : null,
            r.ingredients(),
            r.feedingTarget(),
            r.targetBreedSize() != null ? r.targetBreedSize().getDisplayName() : null,
            r.targetAgeGroup() != null ? r.targetAgeGroup().getDisplayName() : null,
            speciesNames,
            r.feedingMethod(),
            allergens,
            cautions,
            r.consumptionPeriodDisplay(),
            r.shelfLifeAfterOpeningDays(),
            r.storageMethod()
        );

        return new ProductDetailResponse(
            r.productId(), timeDeal, summary, detailInfo);
    }

}
