package com.golajugaenyang.common.core.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class PriceCalculator {

    private static final int PERCENT_SCALE = 0;
    private static final int UNIT_PRICE_SCALE = 0;

    /**
     * 할인율(%)을 정수로 반환
     */
    public static BigDecimal discountRate(BigDecimal originalPrice, BigDecimal price) {
        if (originalPrice == null || price == null
            || originalPrice.compareTo(BigDecimal.ZERO) <= 0
            || originalPrice.compareTo(price) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal diff = originalPrice.subtract(price);
        return diff.divide(originalPrice, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .setScale(PERCENT_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 정규화 수량(g/ml/개) 기준 단가를 반환
     */
    public static BigDecimal unitPrice(BigDecimal price, BigDecimal normalizedQuantityValue) {
        if (price == null || normalizedQuantityValue == null
            || normalizedQuantityValue.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return price.divide(normalizedQuantityValue, UNIT_PRICE_SCALE, RoundingMode.HALF_UP);
    }
}
