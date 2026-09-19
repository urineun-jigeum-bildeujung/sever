package com.golajugaenyang.member.adapter.out.persistence.adapter;

import com.golajugaenyang.member.adapter.out.persistence.mapper.WishlistMapper;
import com.golajugaenyang.member.adapter.out.persistence.repository.WishlistJpaRepository;
import com.golajugaenyang.member.domain.entity.Wishlist;
import com.golajugaenyang.member.domain.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WishlistAdapter implements WishlistRepository {

    private final WishlistJpaRepository wishlistJpaRepo;

    @Override
    public Optional<Wishlist> findByMemberIdAndProductId(Long memberId, Long productId) {
        return wishlistJpaRepo.findByMemberIdAndProductId(memberId, productId)
                .map(WishlistMapper::toDomain);
    }

    @Override
    public List<Wishlist> findByMemberId(Long memberId) {
        return wishlistJpaRepo.findByMemberId(memberId).stream()
                .map(WishlistMapper::toDomain)
                .toList();
    }

    @Override
    public Wishlist save(Wishlist wishlist) {
        return WishlistMapper.toDomain(wishlistJpaRepo.save(WishlistMapper.toJpaEntity(wishlist)));
    }

    @Override
    public void deleteById(Long wishlistId) {
        wishlistJpaRepo.deleteById(wishlistId);
    }

}
