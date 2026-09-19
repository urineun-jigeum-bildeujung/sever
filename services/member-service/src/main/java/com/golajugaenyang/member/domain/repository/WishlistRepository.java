package com.golajugaenyang.member.domain.repository;

import com.golajugaenyang.member.domain.entity.Wishlist;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository {

    Optional<Wishlist> findByMemberIdAndProductId(Long memberId, Long productId);

    List<Wishlist> findByMemberId(Long memberId);

    Wishlist save(Wishlist wishlist);

    void deleteById(Long wishlistId);
}
