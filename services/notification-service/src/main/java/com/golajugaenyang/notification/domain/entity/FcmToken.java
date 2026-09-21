package com.golajugaenyang.notification.domain.entity;

import lombok.Getter;

@Getter
public class FcmToken {

    private Long id;
    private Long memberId;
    private String token;

    public FcmToken(Long id, Long memberId, String token) {
        this.id = id;
        this.memberId = memberId;
        this.token = token;
    }
}
