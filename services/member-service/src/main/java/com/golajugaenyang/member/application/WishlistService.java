package com.golajugaenyang.member.application;

import com.golajugaenyang.member.adapter.in.web.dto.response.WishlistItemResponse;
import com.golajugaenyang.member.adapter.out.client.ProductClient;
import com.golajugaenyang.member.adapter.out.client.dto.ProductInternalItemsResponse;
import com.golajugaenyang.member.domain.entity.Wishlist;
import com.golajugaenyang.member.domain.repository.WishlistRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepo;
    private final ProductClient productClient;

    @Transactional
    public boolean toggleWishlist(Long memberId, Long productId) {

        Optional<Wishlist> existing = wishlistRepo.findByMemberIdAndProductId(memberId, productId);

        if (existing.isPresent()) {
            try {
                wishlistRepo.deleteById(existing.get().getId());
            } catch (EmptyResultDataAccessException e) {
            }
            return false;
        }

        try {
            wishlistRepo.save(new Wishlist(null, memberId, productId, null));
        } catch (DataIntegrityViolationException e) {
        }
        return true;
    }

    public List<WishlistItemResponse> getWishlist(Long memberId, String category) {
        List<Wishlist> wishlists = wishlistRepo.findByMemberId(memberId);
        if (wishlists.isEmpty()) {
            return List.of();
        }

        List<Long> productIds = wishlists.stream().map(Wishlist::getProductId).toList();
        ProductInternalItemsResponse products = productClient.getProducts(productIds);

        // TODO: 리뷰 벌크조회 API 연동 전까지 reviewScore/reviewCount는 임시로 비워둠
        return products.items().stream()
                .filter(item -> category == null || category.equals(item.categoryCode()))
                .map(item -> new WishlistItemResponse(
                        item.productId(), item.thumbnailUrl(), true, item.productName(),
                        item.price(), null, 0
                ))
                .toList();
    }

}
