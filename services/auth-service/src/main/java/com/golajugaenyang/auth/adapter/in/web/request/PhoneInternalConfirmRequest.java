package com.golajugaenyang.auth.adapter.in.web.request;

public record PhoneInternalConfirmRequest(
        String phone,
        int code
) {
}
