package com.golajugaenyang.member.adapter.out.client.dto;

public record PhoneConfirmRequest(
        String phone,
        int code
) {
}
