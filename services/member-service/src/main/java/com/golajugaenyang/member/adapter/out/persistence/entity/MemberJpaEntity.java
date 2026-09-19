package com.golajugaenyang.member.adapter.out.persistence.entity;


import com.golajugaenyang.common.jpa.entity.BaseTimeEntity;
import com.golajugaenyang.member.domain.entity.enums.Carrier;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member", uniqueConstraints = {
    @UniqueConstraint(name = "uk_member_auth_id", columnNames = "auth_id"),
    @UniqueConstraint(name = "uk_member_nickname", columnNames = "nickname")
})
public class MemberJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    private String profileImage;

    private String name;

    private LocalDate birth;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Carrier carrier;

    private LocalDateTime deletedAt;

    @Column(nullable = false)
    private Long authId;
}
