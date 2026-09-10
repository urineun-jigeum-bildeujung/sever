package com.golajugaenyang.common.core.domain;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum CategoryCode {
    FOOD("사료", 1),
    TREAT("간식", 2),
    SUPPLEMENT("영양제", 3);

    private final String displayName;
    private final int sortOrder;

    public static Set<CategoryCode> matchByDisplayName(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(values())
            .filter(c -> c.displayName.contains(keyword))
            .collect(Collectors.toUnmodifiableSet());
    }
}
