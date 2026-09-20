package com.golajugaenyang.review.domain.entity.enums;

import java.util.Set;
import lombok.Getter;

@Getter
public enum ReviewQuestionType {
    PALATABILITY("기호성", Set.of(ReviewAnswer.NEGATIVE, ReviewAnswer.NEUTRAL, ReviewAnswer.POSITIVE)),
    DIGESTION("소화·배변", Set.of(ReviewAnswer.NEGATIVE, ReviewAnswer.NEUTRAL, ReviewAnswer.POSITIVE)),
    FEEDING_CONVENIENCE("급여 편의성", Set.of(ReviewAnswer.NEGATIVE, ReviewAnswer.NEUTRAL, ReviewAnswer.POSITIVE)),
    SKIN_COAT("피부·모질", Set.of(ReviewAnswer.NEGATIVE, ReviewAnswer.NEUTRAL, ReviewAnswer.POSITIVE)),
    WEIGHT_VITALITY("체중·활력", Set.of(ReviewAnswer.NEGATIVE, ReviewAnswer.NEUTRAL, ReviewAnswer.POSITIVE)),
    ALLERGY("알러지 반응", Set.of(ReviewAnswer.NEGATIVE, ReviewAnswer.POSITIVE)); // NEUTRAL 없음

    private final String displayName;
    private final Set<ReviewAnswer> allowedAnswers;

    ReviewQuestionType(String displayName, Set<ReviewAnswer> allowedAnswers) {
        this.displayName = displayName;
        this.allowedAnswers = allowedAnswers;
    }

    public boolean isAllowed(ReviewAnswer answer) {
        return allowedAnswers.contains(answer);
    }
}
