package com.golajugaenyang.order.adapter.out.persistence.cart;


import org.springframework.stereotype.Component;

@Component
public class CartRedisKeyGenerator {

    private static final String PREFIX = "cart:";

    public String generate(Long memberId) {
        return PREFIX + memberId;
    }
}
