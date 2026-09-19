package com.golajugaenyang.member.application;

import com.golajugaenyang.member.domain.entity.Wishlist;
import com.golajugaenyang.member.domain.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepo;

    @Transactional
    public boolean toggleWishlist(Long memberId, Long productId) {

        Optional<Wishlist> existing = wishlistRepo.findByMemberIdAndProductId(memberId, productId);

        if (existing.isPresent()) {
            wishlistRepo.deleteById(existing.get().getId());
            return false;
        }
        wishlistRepo.save(new Wishlist(null, memberId, productId, null));
        return true;
    }

}
