package com.golajugaenyang.auth.domain.entity;

import com.golajugaenyang.auth.domain.entity.enums.Status;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class Auth {

    private Long id;
    private String provider;
    private String socialId;
    private String socialEmail;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Auth(Long id, String provider, String socialId, String socialEmail, Status status,
        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.provider = provider;
        this.socialId = socialId;
        this.socialEmail = socialEmail;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
