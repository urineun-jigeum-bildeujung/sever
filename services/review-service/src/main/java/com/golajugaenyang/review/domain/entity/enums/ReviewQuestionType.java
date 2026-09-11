package com.golajugaenyang.review.domain.entity.enums;

import java.util.Set;

public enum ReviewQuestionType {
    //기호성
    PALATABILITY(Set.of(ReviewAnswer.NEGATIVE, ReviewAnswer.NEUTRAL, ReviewAnswer.POSITIVE)),          // 기호성

    //소화,배변
    DIGESTION(Set.of(ReviewAnswer.NEGATIVE, ReviewAnswer.NEUTRAL, ReviewAnswer.POSITIVE)),              // 소화,배변

    //급여 편의성
    FEEDING_CONVENIENCE(Set.of(ReviewAnswer.NEGATIVE, ReviewAnswer.NEUTRAL, ReviewAnswer.POSITIVE)),    // 급여 편의성

    //피부,모질
    SKIN_COAT(Set.of(ReviewAnswer.NEGATIVE, ReviewAnswer.NEUTRAL, ReviewAnswer.POSITIVE)),              // 피부,모질

    //체중,활력
    WEIGHT_VITALITY(Set.of(ReviewAnswer.NEGATIVE, ReviewAnswer.NEUTRAL, ReviewAnswer.POSITIVE)),        // 체중,활력

    //알러지 반응
    ALLERGY(Set.of(ReviewAnswer.NEGATIVE, ReviewAnswer.POSITIVE));                                      // 알러지 반응 (NEUTRAL 없음)

    private final Set<ReviewAnswer> allowedAnswers;

    ReviewQuestionType(Set<ReviewAnswer> allowedAnswers) {
        this.allowedAnswers = allowedAnswers;
    }

    public boolean isAllowed(ReviewAnswer answer) {
        return allowedAnswers.contains(answer);
    }
}