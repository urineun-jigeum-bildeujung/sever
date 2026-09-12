package com.golajugaenyang.product.adapter.out.persistence;

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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class ProductDetailQueryRepositoryImpl implements ProductDetailQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ProductDetailProjection> findDetailById(Long productId) {
        List<Tuple> rows = queryFactory
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

        Set<String> ingredients = Set.copyOf(entity.getIngredients());
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
