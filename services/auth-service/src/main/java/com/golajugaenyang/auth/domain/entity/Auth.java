package com.golajugaenyang.auth.domain.entity;

import com.golajugaenyang.auth.domain.entity.enums.Status;
import java.time.OffsetDateTime;
import lombok.Getter;

@Getter
public class Auth {

    private Long id;
    private String provider;
    private String socialId;
    private String socialEmail;
    private Status status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Auth(Long id, String provider, String socialId, String socialEmail, Status status,
        OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.provider = provider;
        this.socialId = socialId;
        this.socialEmail = socialEmail;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
