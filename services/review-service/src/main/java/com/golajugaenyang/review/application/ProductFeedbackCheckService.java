package com.golajugaenyang.review.application;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.review.adapter.in.web.dto.response.FeedbackCheckPendingListResponse;
import com.golajugaenyang.review.adapter.out.client.OrderClient;
import com.golajugaenyang.review.adapter.out.client.ProductClient;
import com.golajugaenyang.review.adapter.out.client.dto.ConfirmedItemsResponse.ConfirmedItem;
import com.golajugaenyang.review.adapter.out.client.dto.ProductInternalItemResponse;
import com.golajugaenyang.review.domain.entity.ProductFeedbackCheck;
import com.golajugaenyang.review.domain.entity.enums.FeedbackCheckAnswer;
import com.golajugaenyang.review.domain.entity.enums.FeedbackCheckStatus;
import com.golajugaenyang.review.domain.repository.ProductFeedbackCheckRepository;
import com.golajugaenyang.review.error.ReviewErrorCode;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 구매확정 후 일정 기간(사료|간식 7일, 영양제 30일)이 지난 상품에 대해
 * "잘 맞았어요?" 상태 체크를 받는 기능. 스케줄러로 미리 계산해두지 않고,
 * 메인페이지 조회 시점에 order-service/product-service 데이터를 조합해 즉석 계산한다
 * (order-service에 회원 전체 대상 벌크 조회 API가 없어 배치 방식이 적합하지 않음).
 *
 * 목록 조회(GET)와 제출(POST) 양쪽 모두 {@link #toEligible}로 동일한 자격 판정을 거친다 -
 * 제출 경로가 목록에 없던(아직 기간이 안 지났거나 보류 기간 중인) 항목까지 받아들이지
 * 않도록 하기 위함이다. 답변/보류 자체는 원자적 UPSERT로 처리해 동시 요청 레이스를 막는다.
 */
@Service
@RequiredArgsConstructor
public class ProductFeedbackCheckService {

    private static final int MAX_PENDING_ITEMS = 3;
    private static final Duration FOOD_TREAT_PERIOD = Duration.ofDays(7);
    private static final Duration SUPPLEMENT_PERIOD = Duration.ofDays(30);
    private static final Duration POSTPONE_PERIOD = Duration.ofDays(7);
    private static final String PAID_ITEM_STATUS = "PAID";

    private final OrderClient orderClient;
    private final ProductClient productClient;
    private final ProductFeedbackCheckRepository feedbackCheckRepository;

    public FeedbackCheckPendingListResponse getPendingFeedbackChecks(Long memberId) {
        List<ConfirmedItem> paidItems = getPaidConfirmedItems(memberId);
        if (paidItems.isEmpty()) {
            return new FeedbackCheckPendingListResponse(List.of());
        }

        Map<Long, ProductInternalItemResponse> productsById = getProductsById(paidItems);

        List<Long> orderProductIds = paidItems.stream().map(ConfirmedItem::orderItemId).toList();
        Map<Long, ProductFeedbackCheck> existingChecks =
                feedbackCheckRepository.findExistingByOrderProductIds(orderProductIds);

        Instant now = Instant.now();
        List<EligibleItem> eligible = paidItems.stream()
                .filter(item -> !isAnswered(existingChecks.get(item.orderItemId())))
                .map(item -> toEligible(
                        item, productsById.get(item.productId()), existingChecks.get(item.orderItemId()), now))
                .filter(Objects::nonNull)
                .toList();

        List<FeedbackCheckPendingListResponse.Item> content = eligible.stream()
                .sorted(Comparator.comparing(EligibleItem::confirmedAt))
                .limit(MAX_PENDING_ITEMS)
                .map(item -> new FeedbackCheckPendingListResponse.Item(
                        item.orderProductId(), item.productId(),
                        item.product().productName(), item.product().thumbnailUrl(),
                        item.checkAvailableAt(),
                        item.petId()))
                .toList();

        return new FeedbackCheckPendingListResponse(content);
    }

    public void submitFeedback(
            Long memberId, Long productId, Long orderProductId, boolean postpone, FeedbackCheckAnswer answer
    ) {
        ConfirmedItem target = validateEligibleForSubmission(memberId, productId, orderProductId);

        if (postpone) {
            boolean applied = feedbackCheckRepository.postpone(
                    memberId, productId, orderProductId, Instant.now().plus(POSTPONE_PERIOD));
            if (!applied) {
                throw new AppException(ReviewErrorCode.ALREADY_ANSWERED_FEEDBACK);
            }
            return;
        }

        if (answer == null) {
            throw new AppException(ReviewErrorCode.INVALID_ANSWER);
        }
        boolean applied = feedbackCheckRepository.submitAnswer(
                memberId, productId, orderProductId, target.petId(), answer);
        if (!applied) {
            throw new AppException(ReviewErrorCode.ALREADY_ANSWERED_FEEDBACK);
        }
    }

    /**
     * 소유권뿐 아니라 목록 조회와 동일한 자격(카테고리별 기간 경과, 보류 기간 경과)까지
     * 확인한다 - 그렇지 않으면 목록에 뜨지도 않은 항목을 기간 전에 바로 답변하거나,
     * 보류 중인 항목을 곧바로 재답변/재보류할 수 있게 된다.
     */
    private ConfirmedItem validateEligibleForSubmission(Long memberId, Long productId, Long orderProductId) {
        ConfirmedItem target = getPaidConfirmedItems(memberId).stream()
                .filter(item -> item.orderItemId().equals(orderProductId) && item.productId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new AppException(ReviewErrorCode.INVALID_ORDER_PRODUCT));

        ProductInternalItemResponse product = productClient.getProducts(List.of(productId)).items().stream()
                .findFirst()
                .orElse(null);
        ProductFeedbackCheck existing = feedbackCheckRepository
                .findExistingByOrderProductIds(List.of(orderProductId))
                .get(orderProductId);

        if (toEligible(target, product, existing, Instant.now()) == null) {
            throw new AppException(ReviewErrorCode.FEEDBACK_NOT_AVAILABLE_YET);
        }
        return target;
    }

    private boolean isAnswered(ProductFeedbackCheck existing) {
        return existing != null && existing.getFeedbackCheckStatus() == FeedbackCheckStatus.ANSWERED;
    }

    private List<ConfirmedItem> getPaidConfirmedItems(Long memberId) {
        return orderClient.getConfirmedItems(memberId).items().stream()
                .filter(item -> PAID_ITEM_STATUS.equals(item.itemStatus()))
                .toList();
    }

    private Map<Long, ProductInternalItemResponse> getProductsById(List<ConfirmedItem> paidItems) {
        List<Long> productIds = paidItems.stream().map(ConfirmedItem::productId).distinct().toList();
        return productClient.getProducts(productIds).items().stream()
                .collect(Collectors.toMap(ProductInternalItemResponse::productId, p -> p));
    }

    /**
     * "지금 시점에 카테고리별 기간(또는 보류 기간)이 지났는지"만 판단한다.
     * ANSWERED 여부는 호출부(목록 조회는 사전 필터, 제출은 원자적 UPSERT)에서 처리한다.
     */
    private EligibleItem toEligible(
            ConfirmedItem item, ProductInternalItemResponse product, ProductFeedbackCheck existing, Instant now
    ) {
        if (product == null || item.confirmedAt() == null) {
            return null;
        }

        Duration period = periodFor(product.categoryCode());
        if (period == null) {
            return null;
        }

        Instant confirmedAt = item.confirmedAt().toInstant();
        Instant checkAvailableAt = confirmedAt.plus(period);

        // 보류한 적이 있으면, 보류 시점에 다시 계산된 재노출 시점을 기준으로 판단한다
        if (existing != null && existing.getFeedbackCheckStatus() == FeedbackCheckStatus.POSTPONED
                && existing.getCheckAvailableAt() != null) {
            checkAvailableAt = existing.getCheckAvailableAt();
        }

        if (now.isBefore(checkAvailableAt)) {
            return null;
        }
        return new EligibleItem(
                item.orderItemId(), item.productId(), item.petId(), checkAvailableAt, confirmedAt, product);
    }

    private Duration periodFor(String categoryCode) {
        if (categoryCode == null) {
            return null;
        }
        return switch (categoryCode) {
            case "FOOD", "TREAT" -> FOOD_TREAT_PERIOD;
            case "SUPPLEMENT" -> SUPPLEMENT_PERIOD;
            default -> null;
        };
    }

    private record EligibleItem(
            Long orderProductId, Long productId, Long petId, Instant checkAvailableAt, Instant confirmedAt,
            ProductInternalItemResponse product
    ) {

    }
}
