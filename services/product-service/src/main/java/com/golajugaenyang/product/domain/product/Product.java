package com.golajugaenyang.product.domain.product;

import com.golajugaenyang.common.core.domain.AllergenCode;
import com.golajugaenyang.common.core.domain.CategoryCode;
import com.golajugaenyang.common.core.domain.CautionIngredientCode;
import com.golajugaenyang.common.core.domain.QuantityDimension;
import com.golajugaenyang.common.core.domain.QuantityUnit;
import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.domain.SubcategoryCode;
import com.golajugaenyang.common.core.domain.TargetAgeGroup;
import com.golajugaenyang.common.core.domain.TargetBreedSize;
import com.golajugaenyang.common.jpa.entity.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "products",
    uniqueConstraints = @UniqueConstraint(name = "uq_products_sku", columnNames = "sku"),
    indexes = {
        @Index(name = "idx_products_category_sales",
            columnList = "category_code, is_active, sales_count DESC, id DESC"),
        @Index(name = "idx_products_category_review",
            columnList = "category_code, is_active, review_count DESC, id DESC"),
        @Index(name = "idx_products_category_price",
            columnList = "category_code, is_active, price, id"),
        @Index(name = "idx_products_active_sales",
            columnList = "is_active, sales_count DESC, id DESC"),
        @Index(name = "idx_products_active_review",
            columnList = "is_active, review_count DESC, id DESC"),
        @Index(name = "idx_products_active_price",
            columnList = "is_active, price, id")
    })
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String sku;

    @Column(name = "thumbnail_url", nullable = false, length = 500)
    private String thumbnailUrl;

    @Column(name = "product_group_id", nullable = false)
    private Long productGroupId;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "brand_id")
    private Long brandId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_code", nullable = false, length = 30)
    private CategoryCode categoryCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "subcategory_code", length = 30)
    private SubcategoryCode subcategoryCode;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "product_target_species",
        joinColumns = @JoinColumn(name = "product_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "species", length = 10)
    private Set<Species> targetSpecies = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "product_allergens",
        joinColumns = @JoinColumn(name = "product_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "allergen_code", length = 50)
    private Set<AllergenCode> allergenFlags = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "product_cautions",
        joinColumns = @JoinColumn(name = "product_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "caution_code", length = 50)
    private Set<CautionIngredientCode> cautionFlags = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "product_ingredients",
        joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "ingredient_code", length = 50)
    private Set<String> ingredients = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "target_breed_size", length = 20)
    private TargetBreedSize targetBreedSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_age_group", length = 20)
    private TargetAgeGroup targetAgeGroup;

    @Column(name = "original_price", precision = 12, scale = 2)
    private BigDecimal originalPrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(precision = 12, scale = 2)
    private BigDecimal cost;

    @Column(name = "avg_rating", precision = 3, scale = 2)
    private BigDecimal avgRating = BigDecimal.ZERO;

    @Column(name = "review_count", nullable = false)
    private Integer reviewCount = 0;

    @Column(name = "sales_count", nullable = false)
    private Integer salesCount = 0;

    @Column(name = "net_quantity_value", nullable = false, precision = 12, scale = 3)
    private BigDecimal netQuantityValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "net_quantity_unit", nullable = false, length = 10)
    private QuantityUnit netQuantityUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_dimension", nullable = false, length = 10)
    private QuantityDimension quantityDimension;

    @Column(name = "normalized_quantity_value", nullable = false, precision = 12, scale = 3)
    private BigDecimal normalizedQuantityValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "normalized_quantity_unit", nullable = false, length = 10)
    private QuantityUnit normalizedQuantityUnit;

    @Column(length = 100)
    private String manufacturer;

    @Column(name = "origin_country", length = 50)
    private String originCountry;

    @Column(name = "feeding_target", length = 200)
    private String feedingTarget;

    @Column(name = "feeding_method", columnDefinition = "text")
    private String feedingMethod;

    @Column(name = "storage_method", columnDefinition = "text")
    private String storageMethod;

    @Column(name = "consumption_period_display", length = 100)
    private String consumptionPeriodDisplay;

    @Column(name = "shelf_life_after_opening_days")
    private Integer shelfLifeAfterOpeningDays;

    @Column(name = "is_replenishable", nullable = false)
    private boolean replenishable = true;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<ProductImage> images = new ArrayList<>();

    public static Product create(
        String thumbnailUrl, Long productGroupId, String productName, Long brandId,
        CategoryCode category, SubcategoryCode subcategory,
        BigDecimal price, BigDecimal netQty, QuantityUnit netUnit,
        Set<Species> targetSpecies
    ) {
        Product p = new Product();
        p.thumbnailUrl = thumbnailUrl;
        p.productGroupId = productGroupId;
        p.productName = productName;
        p.brandId = brandId;
        p.categoryCode = category;
        p.subcategoryCode = subcategory;
        p.price = price;
        p.netQuantityValue = netQty;
        p.netQuantityUnit = netUnit;
        p.quantityDimension = netUnit.getDimension();
        p.normalizedQuantityValue = netUnit.toNormalized(netQty);
        p.normalizedQuantityUnit = netUnit.normalizedUnit();
        p.targetSpecies = new HashSet<>(targetSpecies);
        return p;
    }

    public void updateRating(BigDecimal avg, int count) {
        this.avgRating = avg;
        this.reviewCount = count;
    }

    public void addImage(ProductImage image) {
        images.add(image);
        image.assignTo(this);
    }
}
