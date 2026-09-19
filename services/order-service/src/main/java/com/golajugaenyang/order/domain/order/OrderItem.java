package com.golajugaenyang.order.domain.order;

import com.golajugaenyang.common.core.domain.CategoryCode;
import com.golajugaenyang.common.core.domain.QuantityDimension;
import com.golajugaenyang.common.core.domain.QuantityUnit;
import com.golajugaenyang.common.jpa.entity.BaseTimeEntity;
import com.golajugaenyang.order.application.order.port.out.dto.CatalogItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "order_items", indexes = {
    @Index(name = "idx_order_items_order", columnList = "order_id"),
    @Index(name = "idx_order_items_product", columnList = "product_id")
})
public class OrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_items_seq")
    @SequenceGenerator(name = "order_items_seq", sequenceName = "order_items_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "deal_item_id")
    private Long dealItemId;

    @Column(name = "pet_id")
    private Long petId;

    @Column(name = "product_group_id_snapshot", nullable = false)
    private Long productGroupIdSnapshot;

    @Column(name = "product_name_snapshot", nullable = false, length = 200)
    private String productNameSnapshot;

    @Column(name = "thumbnail_url_snapshot", length = 500)
    private String thumbnailUrlSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_code_snapshot", nullable = false, length = 30)
    private CategoryCode categoryCodeSnapshot;

    @Column(name = "is_replenishable_snapshot", nullable = false)
    private boolean replenishableSnapshot;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "unit_discount_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitDiscountAmount = BigDecimal.ZERO;

    @Column(name = "promotion_id", length = 100)
    private String promotionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_status", nullable = false, length = 20)
    private OrderItemStatus itemStatus = OrderItemStatus.PAID;

    @Column(name = "active_claim_status", length = 20)
    private String activeClaimStatus;

    @Column(name = "cancelled_quantity", nullable = false)
    private int cancelledQuantity;

    @Column(name = "returned_quantity", nullable = false)
    private int returnedQuantity;

    @Column(name = "unit_quantity_value_snapshot", nullable = false, precision = 12, scale = 3)
    private BigDecimal unitQuantityValueSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_quantity_unit_snapshot", nullable = false, length = 10)
    private QuantityUnit unitQuantityUnitSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_dimension_snapshot", nullable = false, length = 10)
    private QuantityDimension quantityDimensionSnapshot;

    @Column(name = "normalized_quantity_value_snapshot", nullable = false, precision = 12, scale = 3)
    private BigDecimal normalizedQuantityValueSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "normalized_quantity_unit_snapshot", nullable = false, length = 10)
    private QuantityUnit normalizedQuantityUnitSnapshot;

    public static OrderItem fromCatalogSnapshot(CatalogItem catalog, int quantity) {
        OrderItem item = new OrderItem();
        item.productId = catalog.productId();
        item.dealItemId = catalog.isTimeDeal() ? catalog.dealItemId() : null;
        item.productGroupIdSnapshot = catalog.productGroupId();
        item.productNameSnapshot = catalog.productName();
        item.thumbnailUrlSnapshot = catalog.thumbnailUrl();
        item.categoryCodeSnapshot = CategoryCode.valueOf(catalog.categoryCode());
        item.replenishableSnapshot = catalog.replenishable();
        item.quantity = quantity;
        item.unitPrice = catalog.unitPrice().setScale(2, RoundingMode.HALF_UP);
        item.unitDiscountAmount = catalog.unitDiscountAmount().setScale(2, RoundingMode.HALF_UP);
        item.unitQuantityValueSnapshot = catalog.netQuantityValue();
        item.unitQuantityUnitSnapshot = QuantityUnit.fromSymbol(catalog.netQuantityUnit());
        item.quantityDimensionSnapshot = QuantityDimension.valueOf(catalog.quantityDimension());
        item.normalizedQuantityValueSnapshot = catalog.normalizedQuantityValue();
        item.normalizedQuantityUnitSnapshot =
            QuantityUnit.fromSymbol(catalog.normalizedQuantityUnit());
        return item;
    }


    public int effectiveQuantity() {
        return quantity - cancelledQuantity - returnedQuantity;
    }

    /**
     * 항목 합계 = (단가 - 할인) × 수량
     */
    public BigDecimal lineAmount() {
        return unitPrice.subtract(unitDiscountAmount)
            .multiply(BigDecimal.valueOf(quantity));
    }

    void assignTo(Order o) {
        this.order = o;
    }

    public void updateActiveClaimStatus(String claimStatus) {
        this.activeClaimStatus = claimStatus;
    }

    public void cancelFully() {
        if (effectiveQuantity() <= 0) {
            throw new IllegalStateException("id=%d, 취소할 수 있는 수량이 없습니다.".formatted(getId()));
        }
        this.cancelledQuantity = this.quantity;
        this.itemStatus = OrderItemStatus.CANCELLED;
    }
}
