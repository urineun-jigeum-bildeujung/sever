package com.golajugaenyang.common.core.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AllergenSeverity {
    CRITICAL("치명적"),
    SEVERE("중대"),
    MODERATE("보통");

    private final String displayName;
}
