package com.golajugaenyang.notification.adapter.in.internal.dto;

import jakarta.validation.constraints.NotBlank;

public record NoticeCreateRequest(
        @NotBlank String title,
        @NotBlank String body
) {

}
