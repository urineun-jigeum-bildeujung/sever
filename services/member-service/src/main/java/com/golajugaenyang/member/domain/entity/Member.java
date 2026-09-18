package com.golajugaenyang.member.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Instant;

import com.golajugaenyang.member.domain.entity.enums.Carrier;
import lombok.Getter;

@Getter
public class Member {
    private Long id;
    private String nickname;
    private String profileImage;
    private String name;
    private LocalDate birth;
    private String phone;
    private Carrier carrier;
    private LocalDateTime deletedAt;
    private Instant createdAt;
    private Instant updatedAt;
    private Long authId;

    public Member(Long id, String nickname, String profileImage, String name, LocalDate birth,
        String phone, Carrier carrier, LocalDateTime deletedAt, Instant createdAt, Instant updatedAt, Long authId){
        this.id = id;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.name = name;
        this.birth = birth;
        this.phone = phone;
        this.carrier = carrier;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.authId = authId;
    }

    public Member withPhone(String phone, Carrier carrier) {
        return new Member(id, nickname, profileImage, name, birth, phone, carrier, deletedAt, createdAt, updatedAt, authId);
    }

    public Member update(String nickname, String name, LocalDate birth, String image) {
        return new Member(id, nickname, image, name, birth, phone, carrier, deletedAt, createdAt, updatedAt, authId);
    }

    public Member delete() {
        return new Member(id, nickname, profileImage, name, birth, phone, carrier, LocalDateTime.now(), createdAt, updatedAt, authId);
    }

    public Member reactivate(String nickname) {
        return new Member(id, nickname, null, null, null, null, null, null, createdAt, updatedAt, authId);
    }
}
