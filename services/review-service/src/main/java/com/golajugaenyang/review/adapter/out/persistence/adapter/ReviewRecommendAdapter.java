package com.golajugaenyang.review.adapter.out.persistence.adapter;

import com.golajugaenyang.review.adapter.out.persistence.mapper.ReviewRecommendMapper;
import com.golajugaenyang.review.adapter.out.persistence.repository.ReviewLikeCountProjection;
import com.golajugaenyang.review.adapter.out.persistence.repository.ReviewRecommendJpaRepository;
import com.golajugaenyang.review.domain.entity.ReviewRecommend;
import com.golajugaenyang.review.domain.repository.ReviewRecommendRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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

    @Override
    public Map<Long, Long> countByReviewIdIn(List<Long> reviewIds) {
        return reviewRecommendJpaRepo.countByReviewIdIn(reviewIds).stream()
                .collect(Collectors.toMap(
                        ReviewLikeCountProjection::getReviewId, ReviewLikeCountProjection::getCount));
    }
}
