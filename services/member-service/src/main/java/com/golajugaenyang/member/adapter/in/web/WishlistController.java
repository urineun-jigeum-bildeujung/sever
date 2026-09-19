package com.golajugaenyang.member.adapter.in.web;

import com.golajugaenyang.common.security.annotation.AuthId;
import com.golajugaenyang.member.adapter.in.web.dto.response.WishlistToggleResponse;
import com.golajugaenyang.member.application.MemberService;
import com.golajugaenyang.member.application.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members/me/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final MemberService memberService;
    private final WishlistService wishlistService;

    @PatchMapping("/{productId}")
    public ResponseEntity<WishlistToggleResponse> toggleWishlist(
            @AuthId Long authId,
            @PathVariable Long productId) {
        Long memberId = memberService.getMemberIdByAuthId(authId);
        boolean wished = wishlistService.toggleWishlist(memberId, productId);
        return ResponseEntity.ok(new WishlistToggleResponse(wished));
    }
}
