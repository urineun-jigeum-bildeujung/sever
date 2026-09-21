package com.golajugaenyang.review.adapter.in.web.dto.request;

import com.golajugaenyang.review.domain.entity.enums.FeedbackCheckAnswer;
import jakarta.validation.constraints.NotNull;

public record FeedbackSubmitRequest(
        @NotNull Long orderProductId,
        boolean postpone,
        FeedbackCheckAnswer answer
) {

}
