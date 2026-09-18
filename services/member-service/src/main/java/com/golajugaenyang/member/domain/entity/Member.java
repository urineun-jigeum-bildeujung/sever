package com.golajugaenyang.member.domain.entity;

import java.time.LocalDate;
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
    private Instant createdAt;
    private Instant updatedAt;
    private Long authId;

    public Member(Long id, String nickname, String profileImage, String name, LocalDate birth,
        String phone, Carrier carrier, Instant createdAt, Instant updatedAt, Long authId){
        this.id = id;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.name = name;
        this.birth = birth;
        this.phone = phone;
        this.carrier = carrier;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.authId = authId;
    }

    public Member withPhone(String phone, Carrier carrier) {
        return new Member(id, nickname, profileImage, name, birth, phone, carrier, createdAt, updatedAt, authId);
    }
}
