package com.golajugaenyang.review.adapter.out.persistence.adapter;

import com.golajugaenyang.review.adapter.out.persistence.mapper.ReviewRecommendMapper;
import com.golajugaenyang.review.adapter.out.persistence.repository.ReviewRecommendJpaRepository;
import com.golajugaenyang.review.domain.entity.ReviewRecommend;
import com.golajugaenyang.review.domain.repository.ReviewRecommendRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewRecommendAdapter implements ReviewRecommendRepository {

    private final ReviewRecommendJpaRepository reviewRecommendJpaRepo;

    @Override
    public Optional<ReviewRecommend> findByMemberIdAndReviewId(Long memberId, Long reviewId) {
        return reviewRecommendJpaRepo.findByMemberIdAndReviewId(memberId, reviewId)
                .map(ReviewRecommendMapper::toDomain);
    }

    @Override
    public ReviewRecommend save(ReviewRecommend recommend) {
        return ReviewRecommendMapper.toDomain(
                reviewRecommendJpaRepo.save(ReviewRecommendMapper.toJpaEntity(recommend)));
    }

    @Override
    public void deleteById(Long id) {
        reviewRecommendJpaRepo.deleteById(id);
    }

    @Override
    public long countByReviewId(Long reviewId) {
        return reviewRecommendJpaRepo.countByReviewId(reviewId);
    }
}
