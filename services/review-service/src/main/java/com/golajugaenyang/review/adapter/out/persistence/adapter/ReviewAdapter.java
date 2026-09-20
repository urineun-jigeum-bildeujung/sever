package com.golajugaenyang.review.adapter.out.persistence.adapter;

import com.golajugaenyang.review.adapter.out.persistence.mapper.ReviewMapper;
import com.golajugaenyang.review.adapter.out.persistence.repository.ReviewJpaRepository;
import com.golajugaenyang.review.domain.entity.Review;
import com.golajugaenyang.review.domain.repository.ReviewRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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

    @Override
    public boolean existsById(Long reviewId) {
        return reviewJpaRepo.existsById(reviewId);
    }

    @Override
    public Optional<Review> findById(Long reviewId) {
        return reviewJpaRepo.findById(reviewId).map(ReviewMapper::toDomain);
    }

    @Override
    public List<Long> findRecentReviewIdsWithImageByProductId(Long productId, int limit) {
        return reviewJpaRepo.findRecentReviewIdsWithImageByProductId(productId, PageRequest.of(0, limit));
    }

    @Override
    public List<Review> findByMemberId(Long memberId, int page, int size) {
        return reviewJpaRepo.findByMemberIdOrderByCreatedAtDesc(memberId, PageRequest.of(page, size)).stream()
                .map(ReviewMapper::toDomain)
                .toList();
    }
}
