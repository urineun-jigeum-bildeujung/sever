package com.golajugaenyang.review.application;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.common.storage.ObjectTagConfirmer;
import com.golajugaenyang.common.storage.PresignedUpload;
import com.golajugaenyang.common.storage.PresignedUploadIssuer;
import com.golajugaenyang.review.adapter.in.web.dto.request.ReviewCreateRequest;
import com.golajugaenyang.review.adapter.in.web.dto.response.ReviewImageUploadResponse;
import com.golajugaenyang.review.adapter.out.client.MemberClient;
import com.golajugaenyang.review.adapter.out.client.OrderClient;
import com.golajugaenyang.review.adapter.out.client.dto.PurchaseVerificationResponse;
import com.golajugaenyang.review.domain.entity.Review;
import com.golajugaenyang.review.domain.entity.ReviewImage;
import com.golajugaenyang.review.domain.entity.ReviewQuestion;
import com.golajugaenyang.review.domain.entity.enums.DataOrigin;
import com.golajugaenyang.review.domain.entity.enums.ReviewAnswer;
import com.golajugaenyang.review.domain.entity.enums.ReviewQuestionType;
import com.golajugaenyang.review.domain.repository.ReviewImageRepository;
import com.golajugaenyang.review.domain.repository.ReviewQuestionRepository;
import com.golajugaenyang.review.domain.repository.ReviewRepository;
import com.golajugaenyang.review.error.ReviewErrorCode;
import feign.FeignException;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
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
    private final MemberClient memberClient;
    private final OrderClient orderClient;
    private final PresignedUploadIssuer presignedUploadIssuer;
    private final ObjectTagConfirmer objectTagConfirmer;

    @Transactional
    public Review createReview(Long memberId, ReviewCreateRequest request) {

        if (reviewRepo.existsByMemberIdAndProductId(memberId, request.productId())) {
            throw new AppException(ReviewErrorCode.ALREADY_REVIEWED);
        }

        validatePurchaseConfirmed(memberId, request.productId());

        try {
            memberClient.validatePetOwnership(memberId, request.petId());
        } catch (FeignException.NotFound e) {
            throw new AppException(ReviewErrorCode.INVALID_PET);
        }

        List<String> imageUrls = request.images();
        if (imageUrls != null) {
            imageUrls.forEach(fileUrl -> validateOwnImage(memberId, fileUrl));
        }

        Review review = new Review(null, request.text(), request.starRate(), request.usagePeriod(),
                null, null, null, memberId, request.productId(), request.petId(),
                DataOrigin.REAL, false, null);
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
