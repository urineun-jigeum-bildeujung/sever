package com.golajugaenyang.member.domain.entity;

import java.time.Instant;
import lombok.Getter;

@Getter
public class Wishlist {

    private Long id;
    private Long memberId;
    private Long productId;
    private Instant createdAt;

    public Wishlist(Long id, Long memberId, Long productId, Instant createdAt) {
        this.id = id;
        this.memberId = memberId;
        this.productId = productId;
        this.createdAt = createdAt;
    }
}
