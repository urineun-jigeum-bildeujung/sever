package com.golajugaenyang.product.domain.timedeal;


import com.golajugaenyang.common.jpa.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "time_deal_items",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_deal_product",
        columnNames = {"deal_id", "product_id"}))
public class TimeDealItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deal_id", nullable = false)
    private Long dealId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity_limit", nullable = false)
    private int quantityLimit;

    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity;

    @Column(name = "sold_quantity", nullable = false)
    private int soldQuantity;

    @Column(name = "normal_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal normalPrice;

    @Column(name = "discount_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountRate;

    @Column(name = "discounted_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountedPrice;

    @Column(name = "per_user_quantity_limit", nullable = false)
    private int perUserQuantityLimit;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_status", nullable = false, length = 20)
    private TimeDealItemStatus itemStatus = TimeDealItemStatus.ACTIVE;

    public static TimeDealItem create(
        Long dealId, Long productId, BigDecimal normalPrice, BigDecimal discountedPrice,
        int quantityLimit, BigDecimal discountRate, int perUserLimit) {
        TimeDealItem i = new TimeDealItem();
        i.dealId = dealId;
        i.productId = productId;
        i.quantityLimit = quantityLimit;
        i.normalPrice = normalPrice;
        i.discountRate = discountRate;
        i.discountedPrice = discountedPrice;
        i.perUserQuantityLimit = perUserLimit;
        return i;
    }
}
