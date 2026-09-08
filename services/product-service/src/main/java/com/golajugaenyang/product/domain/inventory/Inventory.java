package com.golajugaenyang.product.domain.inventory;

import com.golajugaenyang.common.jpa.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Id;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "inventories")
public class Inventory extends BaseTimeEntity {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "total_stock", nullable = false)
    private int totalStock;

    @Column(name = "reserved_stock", nullable = false)
    private int reservedStock;

}
