package com.golajugaenyang.auth.adapter.out.persistence.entity;

import com.golajugaenyang.auth.domain.entity.enums.AuthStatus;
import com.golajugaenyang.common.jpa.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "auth", uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "social_id"}))
public class AuthJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String socialId;

    @Column(nullable = false)
    private String socialEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthStatus status;
}
