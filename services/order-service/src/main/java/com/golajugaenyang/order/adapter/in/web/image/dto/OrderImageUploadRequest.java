package com.golajugaenyang.order.adapter.in.web.image.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderImageUploadRequest(
    @NotBlank String extension
) {

}
