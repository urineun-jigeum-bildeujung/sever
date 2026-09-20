package com.golajugaenyang.review.application;

import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.domain.TargetBreedSize;
import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.common.storage.ObjectTagConfirmer;
import com.golajugaenyang.common.storage.PresignedUpload;
import com.golajugaenyang.common.storage.PresignedUploadIssuer;
import com.golajugaenyang.review.adapter.in.web.dto.request.ReviewCreateRequest;
import com.golajugaenyang.review.adapter.in.web.dto.response.FeaturedReviewPhotosResponse;
import com.golajugaenyang.review.adapter.in.web.dto.response.MyReviewListResponse;
import com.golajugaenyang.review.adapter.in.web.dto.response.ReviewDetailResponse;
import com.golajugaenyang.review.adapter.in.web.dto.response.ReviewFilterListResponse;
import com.golajugaenyang.review.adapter.in.web.dto.response.ReviewImageUploadResponse;
import com.golajugaenyang.review.adapter.in.web.dto.response.ReviewPhotosResponse;
import com.golajugaenyang.review.adapter.in.web.dto.response.ReviewRecommendResponse;
import com.golajugaenyang.review.adapter.out.client.MemberClient;
import com.golajugaenyang.review.adapter.out.client.OrderClient;
import com.golajugaenyang.review.adapter.out.client.ProductClient;
import com.golajugaenyang.review.adapter.out.client.dto.NicknameInternalItemResponse;
import com.golajugaenyang.review.adapter.out.client.dto.PetSnapshotResponse;
import com.golajugaenyang.review.adapter.out.client.dto.ProductInternalItemResponse;
import com.golajugaenyang.review.adapter.out.client.dto.ProductInternalItemsResponse;
import com.golajugaenyang.review.adapter.out.client.dto.PurchaseVerificationResponse;
import com.golajugaenyang.review.domain.entity.Review;
import com.golajugaenyang.review.domain.entity.ReviewImage;
import com.golajugaenyang.review.domain.entity.ReviewQuestion;
import com.golajugaenyang.review.domain.entity.ReviewRecommend;
import com.golajugaenyang.review.domain.entity.enums.AgeGroup;
import com.golajugaenyang.review.domain.entity.enums.DataOrigin;
import com.golajugaenyang.review.domain.entity.enums.ReviewAnswer;
import com.golajugaenyang.review.domain.entity.enums.ReviewQuestionType;
import com.golajugaenyang.review.domain.entity.enums.ReviewSortType;
import com.golajugaenyang.review.domain.entity.enums.UsagePeriod;
import com.golajugaenyang.review.domain.repository.ReviewImageRepository;
import com.golajugaenyang.review.domain.repository.ReviewQuestionRepository;
import com.golajugaenyang.review.domain.repository.ReviewRecommendRepository;
import com.golajugaenyang.review.domain.repository.ReviewRepository;
import com.golajugaenyang.review.domain.repository.ReviewSearchCriteria;
import com.golajugaenyang.review.domain.repository.ReviewSearchRepository;
import com.golajugaenyang.review.error.ReviewErrorCode;
import feign.FeignException;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final ReviewQuestionRepository reviewQuestionRepo;
    private final ReviewImageRepository reviewImageRepo;
    private final ReviewRecommendRepository reviewRecommendRepo;
    private final ReviewSearchRepository reviewSearchRepo;
    private final MemberClient memberClient;
    private final OrderClient orderClient;
    private final ProductClient productClient;
    private final PresignedUploadIssuer presignedUploadIssuer;
    private final ObjectTagConfirmer objectTagConfirmer;

    @Transactional
    public Review createReview(Long memberId, ReviewCreateRequest request) {

        if (reviewRepo.existsByMemberIdAndProductId(memberId, request.productId())) {
            throw new AppException(ReviewErrorCode.ALREADY_REVIEWED);
        }

        long distinctQuestionKeys = request.answerValues().stream()
                .map(ReviewCreateRequest.AnswerValue::questionKey)
                .distinct()
                .count();
        if (distinctQuestionKeys != request.answerValues().size()) {
            throw new AppException(ReviewErrorCode.DUPLICATE_QUESTION_KEY);
        }

        validatePurchaseConfirmed(memberId, request.productId());

        PetSnapshotResponse petSnapshot;
        try {
            petSnapshot = memberClient.getPetSnapshot(memberId, request.petId());
        } catch (FeignException.NotFound e) {
            throw new AppException(ReviewErrorCode.INVALID_PET);
        }

        List<String> imageUrls = request.images();
        if (imageUrls != null) {
            imageUrls.forEach(fileUrl -> validateOwnImage(memberId, fileUrl));
        }

        Review review = new Review(null, request.text(), request.starRate(), request.usagePeriod(),
                null, null, null, memberId, request.productId(), request.petId(),
                DataOrigin.REAL, false, null,
                petSnapshot.name(), petSnapshot.species(), petSnapshot.breedId(), petSnapshot.age(),
                petSnapshot.sex(), petSnapshot.isNeutered(), petSnapshot.size(), petSnapshot.weight(),
                petSnapshot.healthConcerns() == null ? Set.of() : Set.copyOf(petSnapshot.healthConcerns()));
        Review savedReview = reviewRepo.save(review);

        List<ReviewQuestion> questions = request.answerValues().stream()
                .map(answerValue -> toReviewQuestion(answerValue, savedReview.getId()))
                .toList();
        reviewQuestionRepo.saveAll(questions);

        if (imageUrls != null && !imageUrls.isEmpty()) {
            List<ReviewImage> images = IntStream.range(0, imageUrls.size())
                    .mapToObj(i -> new ReviewImage(null, imageUrls.get(i), i, savedReview.getId()))
                    .toList();
            reviewImageRepo.saveAll(images);

            imageUrls.forEach(fileUrl -> confirmOwnImageAfterCommit(memberId, fileUrl));
        }

        return savedReview;
    }

    private static final int FEATURED_PHOTO_LIMIT = 4;

    public FeaturedReviewPhotosResponse getFeaturedPhotos(Long productId) {
        List<Long> reviewIds = reviewRepo.findRecentReviewIdsWithImageByProductId(productId, FEATURED_PHOTO_LIMIT);
        if (reviewIds.isEmpty()) {
            return new FeaturedReviewPhotosResponse(List.of());
        }

        Map<Long, String> imageUrlByReviewId = reviewImageRepo.findRepresentativeImagesByReviewIds(reviewIds).stream()
                .collect(Collectors.toMap(ReviewImage::getReviewId, ReviewImage::getImageUrl));

        List<FeaturedReviewPhotosResponse.Photo> photos = reviewIds.stream()
                .map(reviewId -> new FeaturedReviewPhotosResponse.Photo(reviewId, imageUrlByReviewId.get(reviewId)))
                .filter(photo -> photo.imageUrl() != null)
                .toList();

        return new FeaturedReviewPhotosResponse(photos);
    }

    public ReviewPhotosResponse getPhotos(Long productId, int page, int size) {
        long totalCount = reviewImageRepo.countByProductId(productId);
        List<ReviewPhotosResponse.Photo> photos = reviewImageRepo.findByProductId(productId, page, size).stream()
                .map(image -> new ReviewPhotosResponse.Photo(image.getReviewId(), image.getImageUrl()))
                .toList();
        boolean hasNext = (long) (page + 1) * size < totalCount;
        return new ReviewPhotosResponse((int) totalCount, photos, hasNext);
    }

    @Transactional
    public ReviewRecommendResponse toggleRecommend(Long memberId, Long reviewId) {
        if (!reviewRepo.existsById(reviewId)) {
            throw new AppException(ReviewErrorCode.NOT_FOUND);
        }

        Optional<ReviewRecommend> existing = reviewRecommendRepo.findByMemberIdAndReviewId(memberId, reviewId);
        boolean liked;
        if (existing.isPresent()) {
            try {
                reviewRecommendRepo.deleteById(existing.get().getId());
            } catch (EmptyResultDataAccessException e) {
            }
            liked = false;
        } else {
            reviewRecommendRepo.insertIfAbsent(memberId, reviewId);
            liked = true;
        }

        long likeCount = reviewRecommendRepo.countByReviewId(reviewId);
        return new ReviewRecommendResponse(liked, (int) likeCount);
    }

    public MyReviewListResponse getMyReviews(Long memberId, int page, int size) {
        List<Review> fetched = reviewRepo.findByMemberId(memberId, page, size + 1);
        boolean hasNext = fetched.size() > size;
        List<Review> reviews = hasNext ? fetched.subList(0, size) : fetched;

        if (reviews.isEmpty()) {
            return new MyReviewListResponse(List.of(), false);
        }

        List<Long> productIds = reviews.stream().map(Review::getProductId).distinct().toList();
        ProductInternalItemsResponse products = productClient.getProducts(productIds);
        Map<Long, ProductInternalItemResponse> productById = products.items().stream()
                .collect(Collectors.toMap(ProductInternalItemResponse::productId, item -> item));

        List<MyReviewListResponse.Item> items = reviews.stream()
                .map(review -> toMyReviewItem(review, productById.get(review.getProductId())))
                .toList();

        return new MyReviewListResponse(items, hasNext);
    }

    private MyReviewListResponse.Item toMyReviewItem(Review review, ProductInternalItemResponse product) {
        return new MyReviewListResponse.Item(
                review.getId(),
                review.getProductId(),
                product != null ? product.productName() : "",
                product != null ? product.thumbnailUrl() : null,
                (int) Math.round(review.getStarRate()),
                review.getText(),
                review.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDate()
        );
    }

    public ReviewDetailResponse getReviewDetail(Long reviewId, Long memberId) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new AppException(ReviewErrorCode.NOT_FOUND));

        boolean isMine = memberId != null && memberId.equals(review.getMemberId());

        ProductInternalItemsResponse products = productClient.getProducts(List.of(review.getProductId()));
        ProductInternalItemResponse product = products.items().stream().findFirst().orElse(null);
        ReviewDetailResponse.Product productSummary = new ReviewDetailResponse.Product(
                review.getProductId(),
                product != null ? product.productName() : "",
                product != null ? product.thumbnailUrl() : null
        );

        List<ReviewQuestion> questions = reviewQuestionRepo.findByReviewId(reviewId);
        List<ReviewDetailResponse.AnswerValue> answerValues = questions.stream()
                .map(q -> new ReviewDetailResponse.AnswerValue(
                        q.getReviewQuestionType().name(), q.getReviewAnswer().name()))
                .toList();

        List<String> goodPoints = questions.stream()
                .filter(q -> q.getReviewAnswer() == ReviewAnswer.POSITIVE)
                .map(q -> toPointPhrase(q.getReviewQuestionType(), q.getReviewAnswer()))
                .toList();
        List<String> badPoints = questions.stream()
                .filter(q -> q.getReviewAnswer() != ReviewAnswer.POSITIVE)
                .map(q -> toPointPhrase(q.getReviewQuestionType(), q.getReviewAnswer()))
                .toList();

        List<String> images = reviewImageRepo.findByReviewId(reviewId).stream()
                .map(ReviewImage::getImageUrl)
                .toList();

        return new ReviewDetailResponse(
                review.getId(),
                isMine,
                productSummary,
                review.getPetId(),
                (int) Math.round(review.getStarRate()),
                review.getUsagePeriod(),
                answerValues,
                goodPoints.isEmpty() ? null : goodPoints,
                badPoints.isEmpty() ? null : badPoints,
                null,
                review.getText(),
                images.isEmpty() ? null : images,
                review.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDate()
        );
    }

    public ReviewFilterListResponse getProductReviews(
            Long productId, String species, Long breedId, String ageGroup, Boolean neutered,
            Integer weightMin, Integer weightMax, List<String> healthConcerns, String usagePeriod,
            String sort, int page, int size, boolean personalized, Long petId, Long memberId) {

        ReviewSearchCriteria criteria = buildSearchCriteria(productId, species, breedId, ageGroup, neutered,
                weightMin, weightMax, healthConcerns, usagePeriod, sort, page, size, personalized, petId, memberId);

        List<Review> reviews = reviewSearchRepo.search(criteria);
        long totalCount = reviewSearchRepo.count(criteria);
        Double averageRating = reviewSearchRepo.averageRating(productId);
        double roundedAverage = averageRating != null ? averageRating : 0.0;

        if (reviews.isEmpty()) {
            return new ReviewFilterListResponse(roundedAverage, (int) totalCount, List.of());
        }

        List<Long> reviewIds = reviews.stream().map(Review::getId).toList();
        List<Long> reviewerIds = reviews.stream().map(Review::getMemberId).distinct().toList();

        Map<Long, String> nicknameByMemberId = memberClient.getNicknames(reviewerIds).items().stream()
                .collect(Collectors.toMap(NicknameInternalItemResponse::memberId, NicknameInternalItemResponse::nickname));

        Map<Long, Long> likeCountByReviewId = reviewRecommendRepo.countByReviewIdIn(reviewIds);

        Map<Long, List<String>> imagesByReviewId = reviewImageRepo.findByReviewIdIn(reviewIds).stream()
                .collect(Collectors.groupingBy(ReviewImage::getReviewId,
                        Collectors.mapping(ReviewImage::getImageUrl, Collectors.toList())));

        Map<Long, String> palatabilityByReviewId = reviewQuestionRepo.findByReviewIdIn(reviewIds).stream()
                .filter(q -> q.getReviewQuestionType() == ReviewQuestionType.PALATABILITY)
                .collect(Collectors.toMap(ReviewQuestion::getReviewId, q -> toPalatabilityDisplay(q.getReviewAnswer()),
                        (first, second) -> first));

        List<ReviewFilterListResponse.Item> items = reviews.stream()
                .map(review -> new ReviewFilterListResponse.Item(
                        review.getId(),
                        nicknameByMemberId.getOrDefault(review.getMemberId(), ""),
                        new ReviewFilterListResponse.Pet(
                                review.getPetName(),
                                review.getPetSex().name(),
                                review.getPetAge(),
                                review.getPetBreedSize().name(),
                                review.getPetSpecies().name()),
                        (int) Math.round(review.getStarRate()),
                        formatUsagePeriod(review.getUsagePeriod()),
                        palatabilityByReviewId.get(review.getId()),
                        review.getText(),
                        imagesByReviewId.get(review.getId()),
                        likeCountByReviewId.getOrDefault(review.getId(), 0L).intValue(),
                        review.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDate()
                ))
                .toList();

        return new ReviewFilterListResponse(roundedAverage, (int) totalCount, items);
    }

    private ReviewSearchCriteria buildSearchCriteria(
            Long productId, String species, Long breedId, String ageGroup, Boolean neutered,
            Integer weightMin, Integer weightMax, List<String> healthConcerns, String usagePeriod,
            String sort, int page, int size, boolean personalized, Long petId, Long memberId) {

        Species speciesEnum = parseEnum(species, Species.class);
        AgeGroup ageGroupEnum = parseEnum(ageGroup, AgeGroup.class);
        UsagePeriod usagePeriodEnum = parseEnum(usagePeriod, UsagePeriod.class);
        ReviewSortType sortType = sort != null ? parseEnum(sort, ReviewSortType.class) : ReviewSortType.LATEST;

        Species personalizedSpecies = null;
        TargetBreedSize personalizedBreedSize = null;
        if (personalized && petId != null) {
            if (memberId == null) {
                throw new AppException(ReviewErrorCode.INVALID_FILTER);
            }
            PetSnapshotResponse target;
            try {
                target = memberClient.getPetSnapshot(memberId, petId);
            } catch (FeignException.NotFound e) {
                throw new AppException(ReviewErrorCode.INVALID_PET);
            }
            personalizedSpecies = target.species();
            personalizedBreedSize = target.size();
        }

        return new ReviewSearchCriteria(productId, speciesEnum, breedId, ageGroupEnum, neutered,
                weightMin, weightMax, healthConcerns != null ? Set.copyOf(healthConcerns) : null,
                usagePeriodEnum, sortType, page, size, personalizedSpecies, personalizedBreedSize);
    }

    private <E extends Enum<E>> E parseEnum(String value, Class<E> type) {
        if (value == null) {
            return null;
        }
        try {
            return Enum.valueOf(type, value);
        } catch (IllegalArgumentException e) {
            throw new AppException(ReviewErrorCode.INVALID_FILTER);
        }
    }

    private static final int ONE_MONTH_DAYS = 30;
    private static final int THREE_MONTHS_DAYS = 90;
    private static final int SIX_MONTHS_DAYS = 180;
    private static final int ONE_YEAR_DAYS = 365;

    private String formatUsagePeriod(int usagePeriodDays) {
        if (usagePeriodDays < ONE_MONTH_DAYS) {
            return usagePeriodDays + "일";
        }
        if (usagePeriodDays < THREE_MONTHS_DAYS) {
            return (usagePeriodDays / ONE_MONTH_DAYS) + "개월";
        }
        if (usagePeriodDays < ONE_YEAR_DAYS) {
            return (usagePeriodDays / ONE_MONTH_DAYS) + "개월";
        }
        return (usagePeriodDays / ONE_YEAR_DAYS) + "년";
    }

    private String toPalatabilityDisplay(ReviewAnswer answer) {
        return switch (answer) {
            case POSITIVE -> "GOOD";
            case NEUTRAL -> "NORMAL";
            case NEGATIVE -> "BAD";
        };
    }

    private String toPointPhrase(ReviewQuestionType type, ReviewAnswer answer) {
        String suffix = switch (answer) {
            case POSITIVE -> "좋음";
            case NEUTRAL -> "보통";
            case NEGATIVE -> "나쁨";
        };
        return type.getDisplayName() + " " + suffix;
    }

    private void validatePurchaseConfirmed(Long memberId, Long productId) {
        PurchaseVerificationResponse response = orderClient.getPurchaseVerification(memberId, productId);
        boolean confirmed = response.items().stream()
                .anyMatch(item -> "CONFIRMED".equals(item.orderStatus())
                        && !"CANCELLED".equals(item.itemStatus())
                        && !"RETURNED".equals(item.itemStatus()));
        if (!confirmed) {
            throw new AppException(ReviewErrorCode.PURCHASE_NOT_CONFIRMED);
        }
    }

    public ReviewImageUploadResponse issueImageUploadUrl(Long memberId, String extension) {
        try {
            PresignedUpload upload = presignedUploadIssuer.issue("member-" + memberId, extension);
            return new ReviewImageUploadResponse(upload.uploadUrl(), upload.fileUrl());
        } catch (IllegalArgumentException e) {
            throw new AppException(ReviewErrorCode.INVALID_IMAGE_EXTENSION);
        }
    }

    private void validateOwnImage(Long memberId, String fileUrl) {
        try {
            objectTagConfirmer.validateOwnership(fileUrl, "member-" + memberId);
        } catch (IllegalArgumentException e) {
            throw new AppException(ReviewErrorCode.FORBIDDEN_IMAGE);
        }
    }

    private void confirmOwnImageAfterCommit(Long memberId, String fileUrl) {
        String ownerId = "member-" + memberId;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                objectTagConfirmer.confirm(fileUrl, ownerId);
            }
        });
    }

    private ReviewQuestion toReviewQuestion(ReviewCreateRequest.AnswerValue answerValue, Long reviewId) {
        ReviewQuestionType questionType;
        try {
            questionType = ReviewQuestionType.valueOf(answerValue.questionKey());
        } catch (IllegalArgumentException e) {
            throw new AppException(ReviewErrorCode.INVALID_QUESTION_KEY);
        }

        ReviewAnswer answer;
        try {
            answer = ReviewAnswer.valueOf(answerValue.answerValue());
        } catch (IllegalArgumentException e) {
            throw new AppException(ReviewErrorCode.INVALID_ANSWER);
        }

        if (!questionType.isAllowed(answer)) {
            throw new AppException(ReviewErrorCode.INVALID_ANSWER);
        }

        return new ReviewQuestion(null, questionType, answer, null, reviewId);
    }
}
