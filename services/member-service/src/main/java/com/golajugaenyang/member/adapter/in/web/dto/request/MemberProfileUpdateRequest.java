package com.golajugaenyang.member.adapter.in.web.dto.request;

import java.time.LocalDate;

public record MemberProfileUpdateRequest(
        String nickname,
        String name,
        LocalDate birth,
        String image
) {
}
