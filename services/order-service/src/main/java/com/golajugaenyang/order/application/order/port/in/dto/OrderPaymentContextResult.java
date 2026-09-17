package com.golajugaenyang.order.application.order.port.in.dto;

import com.golajugaenyang.order.application.order.port.out.dto.ReservationItem;
import java.math.BigDecimal;
import java.util.List;

public record OrderPaymentContextResult(
    Long orderId,
    String orderNumber,
    Long memberId,
    String orderStatus,
    BigDecimal totalAmount,
    String orderName,
    List<ReservationItem> items
) {

}
