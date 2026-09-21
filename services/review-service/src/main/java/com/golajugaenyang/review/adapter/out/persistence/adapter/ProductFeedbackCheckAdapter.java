package com.golajugaenyang.review.adapter.out.persistence.adapter;

import com.golajugaenyang.review.adapter.out.persistence.entity.ProductFeedbackCheckJpaEntity;
import com.golajugaenyang.review.adapter.out.persistence.mapper.ProductFeedbackCheckMapper;
import com.golajugaenyang.review.adapter.out.persistence.repository.ProductFeedbackCheckJpaRepository;
import com.golajugaenyang.review.domain.entity.ProductFeedbackCheck;
import com.golajugaenyang.review.domain.entity.enums.FeedbackCheckAnswer;
import com.golajugaenyang.review.domain.entity.enums.FeedbackCheckStatus;
import com.golajugaenyang.review.domain.repository.ProductFeedbackCheckRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    public boolean isAlreadyAnswered(Long orderProductId) {
        return feedbackCheckJpaRepo.findByOrderProductId(orderProductId)
                .map(entity -> entity.getFeedbackCheckStatus() == FeedbackCheckStatus.ANSWERED)
                .orElse(false);
    }

    @Override
    @Transactional
    public void submitAnswer(Long memberId, Long productId, Long orderProductId, FeedbackCheckAnswer answer) {
        ProductFeedbackCheckJpaEntity entity = findOrCreate(memberId, productId, orderProductId);
        entity.setFeedbackCheckAnswer(answer);
        entity.setFeedbackCheckStatus(FeedbackCheckStatus.ANSWERED);
        entity.setAnsweredAt(Instant.now());
    }

    @Override
    @Transactional
    public void postpone(Long memberId, Long productId, Long orderProductId, Instant postponedUntil) {
        ProductFeedbackCheckJpaEntity entity = findOrCreate(memberId, productId, orderProductId);
        entity.setFeedbackCheckStatus(FeedbackCheckStatus.POSTPONED);
        entity.setCheckAvailableAt(postponedUntil);
    }

    private ProductFeedbackCheckJpaEntity findOrCreate(Long memberId, Long productId, Long orderProductId) {
        Supplier<ProductFeedbackCheckJpaEntity> createNew = () -> feedbackCheckJpaRepo.save(
                ProductFeedbackCheckJpaEntity.builder()
                        .memberId(memberId)
                        .productId(productId)
                        .orderProductId(orderProductId)
                        .feedbackCheckStatus(FeedbackCheckStatus.PENDING)
                        .build());
        return feedbackCheckJpaRepo.findByOrderProductId(orderProductId).orElseGet(createNew);
    }
}
