package com.golajugaenyang.order.application.cart.port.out;

import com.golajugaenyang.order.domain.cart.CartItem;
import com.golajugaenyang.order.domain.cart.CartItemKey;
import java.time.Duration;
import java.util.List;

public interface CartRepository {

    int NOT_FOUND = -1;

    List<CartItem> findAll(Long memberId);

    int addOrIncrease(Long memberId, CartItemKey key, int quantity, Duration ttl);

    int changeQuantity(Long memberId, CartItemKey key, int delta, Duration ttl);

    void remove(Long memberId, CartItemKey key);
}
