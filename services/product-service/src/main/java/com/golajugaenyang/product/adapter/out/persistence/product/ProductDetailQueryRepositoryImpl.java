package com.golajugaenyang.product.adapter.out.persistence.product;

import static com.golajugaenyang.product.domain.product.QBrand.brand;
import static com.golajugaenyang.product.domain.product.QProduct.product;
import static com.golajugaenyang.product.domain.product.QProductImage.productImage;

import com.golajugaenyang.common.core.domain.AllergenCode;
import com.golajugaenyang.common.core.domain.CautionIngredientCode;
import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.product.application.product.port.out.ProductDetailQueryRepository;
import com.golajugaenyang.product.application.product.port.out.dto.ProductDetailProjection;
import com.golajugaenyang.product.domain.product.Product;
import com.golajugaenyang.product.domain.product.ProductImage;
import com.golajugaenyang.product.domain.product.ProductStatus;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class ProductDetailQueryRepositoryImpl implements ProductDetailQueryRepository {

    // QueryDSL(JPAQueryFactory)로 직접 조회하는 구조라 Spring Data 레포지토리
    // 메트릭(spring_data_repository_invocations_seconds)이 자동으로 안 잡힌다 —
    // 이 쿼리만 수동으로 타이머를 감싸서 노출한다.
    private final JPAQueryFactory queryFactory;
    private final MeterRegistry meterRegistry;

    @Override
    public Optional<ProductDetailProjection> findDetailById(Long productId) {
        Timer.Sample sample = Timer.start(meterRegistry);
        List<Tuple> rows;
        try {
            rows = queryFactory
                .select(product, brand.name)
                .from(product)
                .leftJoin(brand).on(product.brandId.eq(brand.id))
                .leftJoin(product.images, productImage)
                .fetchJoin()
                .where(
                    product.id.eq(productId),
                    product.status.in(ProductStatus.ON_SALE, ProductStatus.SOLD_OUT)
                )
                .distinct()
                .fetch();
        } finally {
            sample.stop(Timer.builder("product.detail.query").register(meterRegistry));
        }

        if (rows.isEmpty()) {
            return Optional.empty();
        }

        Tuple row = rows.getFirst();
        return Optional.of(
            toProjection(Objects.requireNonNull(row.get(product)), row.get(brand.name)));
    }

    private ProductDetailProjection toProjection(Product entity, String brandName) {
        List<String> imageUrls = entity.getImages().stream()
            .map(ProductImage::getImageUrl)
            .toList();

        List<String> ingredients = List.copyOf(entity.getIngredients());
        Set<AllergenCode> allergenFlags = Set.copyOf(entity.getAllergenFlags());
        Set<CautionIngredientCode> cautionFlags = Set.copyOf(entity.getCautionFlags());
        Set<Species> targetSpecies = Set.copyOf(entity.getTargetSpecies());

        return new ProductDetailProjection(
            entity.getId(), entity.getThumbnailUrl(), imageUrls, entity.getProductName(),
            entity.getPrice(), entity.getOriginalPrice(),
            entity.getAvgRating(), entity.getReviewCount(), entity.getStatus(),
            entity.getManufacturer(), brandName, entity.getOriginCountry(),
            entity.getNetQuantityValue(), entity.getNetQuantityUnit(),
            ingredients, entity.getFeedingTarget(),
            entity.getTargetBreedSize(), entity.getTargetAgeGroup(), targetSpecies,
            entity.getFeedingMethod(), allergenFlags, cautionFlags,
            entity.getConsumptionPeriodDisplay(), entity.getShelfLifeAfterOpeningDays(),
            entity.getStorageMethod()
        );
    }
}
