package com.golajugaenyang.review.error;

import com.golajugaenyang.common.core.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {

    INVALID_PET(
            HttpStatus.BAD_REQUEST,
            "REVIEW_400_INVALID_PET", "존재하지 않거나 본인 소유가 아닌 반려동물입니다."
    ),
    ALREADY_REVIEWED(
            HttpStatus.CONFLICT,
            "REVIEW_409_ALREADY_REVIEWED", "이미 이 상품에 대한 리뷰를 작성했습니다."
    ),
    INVALID_QUESTION_KEY(
            HttpStatus.BAD_REQUEST,
            "REVIEW_400_INVALID_QUESTION_KEY", "존재하지 않는 질문 항목입니다."
    ),
    INVALID_ANSWER(
            HttpStatus.BAD_REQUEST,
            "REVIEW_400_INVALID_ANSWER", "해당 질문에 허용되지 않는 답변입니다."
    ),
    INVALID_IMAGE_EXTENSION(
            HttpStatus.BAD_REQUEST,
            "REVIEW_400_INVALID_IMAGE_EXTENSION", "허용되지 않는 이미지 확장자입니다."
    ),
    FORBIDDEN_IMAGE(
            HttpStatus.FORBIDDEN,
            "REVIEW_403_FORBIDDEN_IMAGE", "본인이 업로드한 이미지만 사용할 수 있습니다."
    ),
    PURCHASE_NOT_CONFIRMED(
            HttpStatus.FORBIDDEN,
            "REVIEW_403_PURCHASE_NOT_CONFIRMED", "구매 확정된 상품에 대해서만 리뷰를 작성할 수 있습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
