package com.golajugaenyang.product.domain.product;


import com.golajugaenyang.common.core.domain.CategoryCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "product_groups")
public class ProductGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "canonical_product_name", nullable = false, length = 200)
    private String canonicalProductName;

    @Column(name = "brand_id")
    private Long brandId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_code", nullable = false, length = 30)
    private CategoryCode categoryCode;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public static ProductGroup autoCreateFor(
        String productName, Long brandId, CategoryCode category) {
        ProductGroup g = new ProductGroup();
        g.canonicalProductName = productName;
        g.brandId = brandId;
        g.categoryCode = category;
        return g;
    }
}
