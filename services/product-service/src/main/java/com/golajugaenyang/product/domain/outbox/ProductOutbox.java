package com.golajugaenyang.product.domain.outbox;


import com.golajugaenyang.common.jpa.entity.BaseOutboxEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "product_outbox",
    indexes = @Index(name = "idx_product_outbox_status", columnList = "status, created_at"))
public class ProductOutbox extends BaseOutboxEntity {

}
