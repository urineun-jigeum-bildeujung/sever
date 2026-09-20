package com.golajugaenyang.review.adapter.in.web.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ReviewCreateRequest(
        @NotNull Long productId,
        @NotNull Long petId,
        @NotNull @DecimalMin("1.0") @DecimalMax("5.0") Double starRate,
        @NotBlank String usagePeriod,
        @NotEmpty List<@Valid AnswerValue> answerValues,
        @NotBlank @Size(max = 300) String text,
        @Size(max = 3) List<String> images
) {
    public record AnswerValue(
            @NotBlank String questionKey,
            @NotBlank String answerValue
    ) {
    }
}
