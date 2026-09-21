package com.golajugaenyang.review.adapter.out.persistence.adapter;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.review.adapter.out.persistence.mapper.ReviewMapper;
import com.golajugaenyang.review.adapter.out.persistence.repository.ReviewJpaRepository;
import com.golajugaenyang.review.domain.entity.Review;
import com.golajugaenyang.review.domain.repository.ReviewRepository;
import com.golajugaenyang.review.error.ReviewErrorCode;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewAdapter implements ReviewRepository {

    private static final String MEMBER_PRODUCT_UNIQUE_CONSTRAINT = "uk_review_member_product";

    private final ReviewJpaRepository reviewJpaRepo;

    @Override
    public Review save(Review review) {
        try {
            return ReviewMapper.toDomain(reviewJpaRepo.save(ReviewMapper.toJpaEntity(review)));
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException cve
                    && MEMBER_PRODUCT_UNIQUE_CONSTRAINT.equals(cve.getConstraintName())) {
                throw new AppException(ReviewErrorCode.ALREADY_REVIEWED);
            }
            throw e;
        }
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

    @Override
    public List<Long> findReviewedProductIds(Long memberId, List<Long> productIds) {
        return reviewJpaRepo.findReviewedProductIds(memberId, productIds);
    }
}
