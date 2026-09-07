package com.golajugaenyang.common.core.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum QuantityUnit {
    G(QuantityDimension.MASS, "g", BigDecimal.ONE),
    KG(QuantityDimension.MASS, "kg", BigDecimal.valueOf(1000)),
    ML(QuantityDimension.VOLUME, "ml", BigDecimal.ONE),
    L(QuantityDimension.VOLUME, "L", BigDecimal.valueOf(1000)),
    EA(QuantityDimension.COUNT, "개", BigDecimal.ONE);

    private final QuantityDimension dimension;
    private final String symbol;
    private final BigDecimal factor;

    /**
     * 표준 단위(G/ML/EA) 기준 값으로 환산. 예: 1.2 KG → 1200 G
     */
    public BigDecimal toNormalized(BigDecimal value) {
        return value.multiply(factor);
    }

    /**
     * 이 단위의 표준 단위. KG → G, L → ML, EA → EA
     */
    public QuantityUnit normalizedUnit() {
        return switch (dimension) {
            case MASS -> G;
            case VOLUME -> ML;
            case COUNT -> EA;
        };
    }
}
