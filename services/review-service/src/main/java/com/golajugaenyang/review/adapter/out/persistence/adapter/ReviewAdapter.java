package com.golajugaenyang.review.adapter.out.persistence.adapter;

import com.golajugaenyang.review.adapter.out.persistence.mapper.ReviewMapper;
import com.golajugaenyang.review.adapter.out.persistence.repository.ReviewJpaRepository;
import com.golajugaenyang.review.domain.entity.Review;
import com.golajugaenyang.review.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewAdapter implements ReviewRepository {

    private final ReviewJpaRepository reviewJpaRepo;

    @Override
    public Review save(Review review) {
        return ReviewMapper.toDomain(reviewJpaRepo.save(ReviewMapper.toJpaEntity(review)));
    }

    @Override
    public boolean existsByMemberIdAndProductId(Long memberId, Long productId) {
        return reviewJpaRepo.existsByMemberIdAndProductId(memberId, productId);
    }
}
