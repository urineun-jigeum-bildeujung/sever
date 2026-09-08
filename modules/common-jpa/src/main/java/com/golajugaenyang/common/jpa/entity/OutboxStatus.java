package com.golajugaenyang.common.jpa.entity;

import lombok.Getter;

@Getter
public enum OutboxStatus {
    PENDING,
    SENT,
    FAILED
}
