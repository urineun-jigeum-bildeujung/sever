package com.golajugaenyang.member.adapter.out.persistence.entity;

import com.golajugaenyang.common.jpa.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "address")
public class AddressJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String addressName;

    @Column(nullable = false)
    private String receiver;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String addressDetail;

    private boolean isDefault;

    private String deliveryNode;

    @Column(nullable = false)
    private Long memberId;
}



