package com.golajugaenyang.member.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class Member {
    private Long id;
    private String nickname;
    private String profileImage;
    private String name;
    private LocalDate birth;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long authId;

    public Member(Long id, String nickname, String profileImage, String name, LocalDate birth,
        String phone, LocalDateTime createdAt, LocalDateTime updatedAt, Long authId){
        this.id = id;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.name = name;
        this.birth = birth;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.authId = authId;
    }

}
