package com.golajugaenyang.review.application;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.review.adapter.in.web.dto.request.ReviewCreateRequest;
import com.golajugaenyang.review.adapter.out.client.MemberClient;
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

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final ReviewQuestionRepository reviewQuestionRepo;
    private final ReviewImageRepository reviewImageRepo;
    private final MemberClient memberClient;

    @Transactional
    public Review createReview(Long memberId, ReviewCreateRequest request) {

        if (reviewRepo.existsByMemberIdAndProductId(memberId, request.productId())) {
            throw new AppException(ReviewErrorCode.ALREADY_REVIEWED);
        }

        try {
            memberClient.validatePetOwnership(memberId, request.petId());
        } catch (FeignException.NotFound e) {
            throw new AppException(ReviewErrorCode.INVALID_PET);
        }

        Review review = new Review(null, request.text(), request.starRate(), request.usagePeriod(),
                null, null, null, memberId, request.productId(), request.petId(),
                DataOrigin.REAL, false, null);
        Review savedReview = reviewRepo.save(review);

        List<ReviewQuestion> questions = request.answerValues().stream()
                .map(answerValue -> toReviewQuestion(answerValue, savedReview.getId()))
                .toList();
        reviewQuestionRepo.saveAll(questions);

        if (request.images() != null && !request.images().isEmpty()) {
            List<String> imageUrls = request.images();
            List<ReviewImage> images = IntStream.range(0, imageUrls.size())
                    .mapToObj(i -> new ReviewImage(null, imageUrls.get(i), i, savedReview.getId()))
                    .toList();
            reviewImageRepo.saveAll(images);
        }

        return savedReview;
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
