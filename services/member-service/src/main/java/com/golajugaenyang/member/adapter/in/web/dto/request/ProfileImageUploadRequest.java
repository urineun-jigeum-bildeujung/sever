package com.golajugaenyang.member.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProfileImageUploadRequest(
        @NotBlank String extension
) {
}
