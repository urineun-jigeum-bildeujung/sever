package com.golajugaenyang.review.adapter.in.web.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ReviewCreateRequest(
        @NotNull Long productId,
        @NotEmpty List<Long> petIds,
        @NotNull @DecimalMin("1.0") @DecimalMax("5.0") Double starRate,
        @NotNull @Positive Integer usagePeriod,
        @NotEmpty List<@Valid AnswerValue> answerValues,
        @NotBlank @Size(max = 300) String text,
        @Size(max = 3) List<String> images
) {
    @AssertTrue(message = "별점은 0.5 단위로 입력해야 합니다.")
    public boolean isStarRateHalfStep() {
        return starRate == null || (starRate * 2) % 1 == 0;
    }

    public record AnswerValue(
            @NotBlank String questionKey,
            @NotBlank String answerValue
    ) {
    }
}
