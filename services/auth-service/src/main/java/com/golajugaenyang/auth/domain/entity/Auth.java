package com.golajugaenyang.auth.domain.entity;

import com.golajugaenyang.auth.domain.entity.enums.AuthStatus;
import java.time.Instant;
import lombok.Getter;

@Getter
public class Auth {

    private Long id;
    private String provider;
    private String socialId;
    private String socialEmail;
    private AuthStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public Auth(Long id, String provider, String socialId, String socialEmail, AuthStatus status,
        Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.provider = provider;
        this.socialId = socialId;
        this.socialEmail = socialEmail;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
