package com.golajugaenyang.review.adapter.out.persistence.adapter;

import com.golajugaenyang.review.adapter.out.persistence.entity.ProductFeedbackCheckJpaEntity;
import com.golajugaenyang.review.adapter.out.persistence.mapper.ProductFeedbackCheckMapper;
import com.golajugaenyang.review.adapter.out.persistence.repository.ProductFeedbackCheckJpaRepository;
import com.golajugaenyang.review.domain.entity.ProductFeedbackCheck;
import com.golajugaenyang.review.domain.entity.enums.FeedbackCheckAnswer;
import com.golajugaenyang.review.domain.repository.ProductFeedbackCheckRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductFeedbackCheckAdapter implements ProductFeedbackCheckRepository {

    private final ProductFeedbackCheckJpaRepository feedbackCheckJpaRepo;

    @Override
    public Map<Long, ProductFeedbackCheck> findExistingByOrderProductIds(List<Long> orderProductIds) {
        return feedbackCheckJpaRepo.findByOrderProductIdIn(orderProductIds).stream()
                .collect(Collectors.toMap(
                        ProductFeedbackCheckJpaEntity::getOrderProductId,
                        ProductFeedbackCheckMapper::toDomain));
    }

    @Override
    public boolean submitAnswer(Long memberId, Long productId, Long orderProductId, FeedbackCheckAnswer answer) {
        return feedbackCheckJpaRepo.upsertAnswer(memberId, productId, orderProductId, answer.name()) > 0;
    }

    @Override
    public boolean postpone(Long memberId, Long productId, Long orderProductId, Instant postponedUntil) {
        return feedbackCheckJpaRepo.upsertPostpone(memberId, productId, orderProductId, postponedUntil) > 0;
    }
}
