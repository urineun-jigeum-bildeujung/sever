package com.golajugaenyang.notification.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FcmTokenRegisterRequest(
        @NotBlank String token
) {

}
