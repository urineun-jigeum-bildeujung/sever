package com.golajugaenyang.review.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReviewImageUploadRequest(
        @NotBlank String extension
) {
}
