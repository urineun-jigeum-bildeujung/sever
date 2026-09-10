package com.golajugaenyang.common.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CategoryCodeTest {

    @Test
    @DisplayName("표시명에 키워드가 포함된 카테고리를 찾는다.")
    void matches_category_by_display_name_substring() {
        Set<CategoryCode> result = CategoryCode.matchByDisplayName("간");

        assertThat(result).containsExactly(CategoryCode.TREAT);
    }

    @Test
    @DisplayName("키워드와 일치하는 카테고리가 없으면 빈 집합을 반환한다")
    void returns_empty_set_when_no_category_matches() {
        assertThat(CategoryCode.matchByDisplayName("고양이")).isEmpty();
    }
}
