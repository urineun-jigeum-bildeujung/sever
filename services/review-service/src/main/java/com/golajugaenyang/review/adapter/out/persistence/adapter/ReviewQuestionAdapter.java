package com.golajugaenyang.review.adapter.out.persistence.adapter;

import com.golajugaenyang.review.adapter.out.persistence.entity.ReviewQuestionJpaEntity;
import com.golajugaenyang.review.adapter.out.persistence.mapper.ReviewQuestionMapper;
import com.golajugaenyang.review.adapter.out.persistence.repository.ReviewQuestionJpaRepository;
import com.golajugaenyang.review.domain.entity.ReviewQuestion;
import com.golajugaenyang.review.domain.repository.ReviewQuestionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewQuestionAdapter implements ReviewQuestionRepository {

    private final ReviewQuestionJpaRepository reviewQuestionJpaRepo;

    @Override
    public List<ReviewQuestion> saveAll(List<ReviewQuestion> questions) {
        List<ReviewQuestionJpaEntity> jpaEntities = questions.stream()
                .map(ReviewQuestionMapper::toJpaEntity)
                .toList();
        return reviewQuestionJpaRepo.saveAll(jpaEntities).stream()
                .map(ReviewQuestionMapper::toDomain)
                .toList();
    }

    @Override
    public List<ReviewQuestion> findByReviewId(Long reviewId) {
        return reviewQuestionJpaRepo.findByReviewId(reviewId).stream()
                .map(ReviewQuestionMapper::toDomain)
                .toList();
    }
}
