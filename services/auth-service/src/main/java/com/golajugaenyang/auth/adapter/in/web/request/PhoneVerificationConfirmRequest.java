package com.golajugaenyang.auth.adapter.in.web.request;

import jakarta.validation.constraints.NotBlank;

public record PhoneVerificationConfirmRequest(@NotBlank String phone, int code) {

}
